package no.ikov.paymentservice.application;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import no.ikov.paymentservice.domain.model.Payment;
import no.ikov.paymentservice.domain.model.PaymentStatus;
import no.ikov.paymentservice.domain.model.Price;
import no.ikov.paymentservice.domain.repository.PaymentRepository;
import no.ikov.paymentservice.domain.repository.PaymentSpecification;
import no.ikov.paymentservice.infrastructure.dto.PaymentRequest;
import no.ikov.paymentservice.infrastructure.dto.PaymentResponse;
import no.ikov.paymentservice.infrastructure.dto.UpdatePaymentStatusRequest;
import no.ikov.paymentservice.infrastructure.exceptions.PaymentNotFoundException;
import no.ikov.paymentservice.infrastructure.exceptions.PaymentServiceUnavailableException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final String CIRCUIT_BREAKER_NAME = "paymentServiceInternal";

    private final PaymentRepository paymentRepository;

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "createPaymentFallback")
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

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "updateStatusFallback")
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
    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new PaymentNotFoundException(id);
        }
        paymentRepository.deleteById(id);
    }

    private PaymentResponse createPaymentFallback(PaymentRequest request, Throwable ex) {
        throw new PaymentServiceUnavailableException("Payment service is currently unavailable");
    }

    private PaymentResponse updateStatusFallback(Long id, UpdatePaymentStatusRequest request, Throwable ex) {
        throw new PaymentServiceUnavailableException("Payment service is currently unavailable");
    }
}
