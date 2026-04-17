package no.ikov.paymentservice.application;

import lombok.RequiredArgsConstructor;
import no.ikov.paymentservice.domain.model.Payment;
import no.ikov.paymentservice.domain.model.PaymentMethod;
import no.ikov.paymentservice.domain.model.Price;
import no.ikov.paymentservice.domain.repository.PaymentRepository;
import no.ikov.paymentservice.infrastructure.dto.PaymentRequest;
import no.ikov.paymentservice.infrastructure.dto.PaymentResponse;
import no.ikov.paymentservice.infrastructure.dto.UpdatePaymentStatusRequest;
import no.ikov.paymentservice.infrastructure.exceptions.PaymentNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {
        Price price = new Price(
                request.getPrice().getAmount(),
                Currency.getInstance(request.getPrice().getCurrency())
        );
        Payment payment = Payment.create(
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

    @Transactional
    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new PaymentNotFoundException(id);
        }
        paymentRepository.deleteById(id);
    }
}
