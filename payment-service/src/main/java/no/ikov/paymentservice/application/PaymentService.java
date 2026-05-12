package no.ikov.paymentservice.application;

import lombok.RequiredArgsConstructor;
import no.ikov.paymentservice.domain.model.Payment;
import no.ikov.paymentservice.domain.model.PaymentMethod;
import no.ikov.paymentservice.domain.model.PaymentStatus;
import no.ikov.paymentservice.domain.model.Price;
import no.ikov.paymentservice.domain.repository.PaymentRepository;
import no.ikov.paymentservice.domain.repository.PaymentSpecification;
import no.ikov.paymentservice.infrastructure.dto.PaymentRequest;
import no.ikov.paymentservice.infrastructure.dto.PaymentResponse;
import no.ikov.paymentservice.infrastructure.dto.UpdatePaymentStatusRequest;
import no.ikov.paymentservice.infrastructure.exceptions.PaymentNotFoundException;
import no.ikov.paymentservice.integration.order.rabbitmq.config.RabbitMQPaymentConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public void processPaymentRequest(PaymentRequest request) {
        PaymentResponse created = createPayment(request);

        UpdatePaymentStatusRequest statusRequest;
        if (ThreadLocalRandom.current().nextInt(100) < 70) {
            statusRequest = new UpdatePaymentStatusRequest(PaymentStatus.COMPLETED, UUID.randomUUID().toString());
        } else {
            statusRequest = new UpdatePaymentStatusRequest(PaymentStatus.FAILED, null);
        }

        PaymentResponse result = updateStatus(created.getId(), statusRequest);
        rabbitTemplate.convertAndSend(
                RabbitMQPaymentConfig.RESULT_EXCHANGE,
                RabbitMQPaymentConfig.RESULT_ROUTING_KEY,
                result
        );
    }

    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {
        Price price = new Price(
                request.getPrice().getAmount(),
                Currency.getInstance(request.getPrice().getCurrency())
        );
        Payment payment = new Payment(
                request.getOrderId(),
                request.getCustomerId(),
                price,
                request.getMethod()
        );
        return PaymentResponse.from(paymentRepository.save(payment));
    }

    @Transactional
    public PaymentResponse updateStatus(Long id, UpdatePaymentStatusRequest request) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        switch (request.getStatus()) {
            case COMPLETED -> {
                if (request.getTransactionId() == null || request.getTransactionId().isBlank()) {
                    throw new IllegalArgumentException("transactionId is required when completing a payment");
                }
                payment.complete(request.getTransactionId());
            }
            case FAILED -> payment.fail();
            case REFUNDED -> payment.refund();
            default -> throw new IllegalArgumentException("Cannot transition to status: " + request.getStatus());
        }

        return PaymentResponse.from(paymentRepository.save(payment));
    }

    public PaymentResponse getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .map(PaymentResponse::from)
                .orElseThrow(() -> new PaymentNotFoundException(id));
    }

    public Page<PaymentResponse> getAllPayments(Long customerId, Long orderId, PaymentStatus status, Pageable pageable) {
        Specification<Payment> spec = Specification
                .where(PaymentSpecification.hasCustomerId(customerId))
                .and(PaymentSpecification.hasOrderId(orderId))
                .and(PaymentSpecification.hasStatus(status));
        return paymentRepository.findAll(spec, pageable).map(PaymentResponse::from);
    }

    @Transactional
    public Payment createAndProcessPayment(Long orderId, Long customerId, java.math.BigDecimal amount, String currency, String paymentMethod) {
        Price price = new Price(amount, Currency.getInstance(currency));
        Payment payment = new Payment(orderId, customerId, price, PaymentMethod.valueOf(paymentMethod));
        payment = paymentRepository.save(payment);
        if (ThreadLocalRandom.current().nextInt(100) < 70) {
            payment.complete(UUID.randomUUID().toString());
        } else {
            payment.fail();
        }
        return paymentRepository.save(payment);
    }

    @Transactional
    public void refundByOrderId(Long orderId) {
        paymentRepository.findByOrderId(orderId).ifPresent(payment -> {
            if (payment.getStatus() == PaymentStatus.COMPLETED) {
                payment.refund();
                paymentRepository.save(payment);
            }
        });
    }

    @Transactional
    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new PaymentNotFoundException(id);
        }
        paymentRepository.deleteById(id);
    }


}
