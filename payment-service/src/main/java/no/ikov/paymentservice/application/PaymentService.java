package no.ikov.paymentservice.application;

import lombok.RequiredArgsConstructor;
import no.ikov.paymentservice.domain.model.Payment;
import no.ikov.paymentservice.domain.model.PaymentMethod;
import no.ikov.paymentservice.domain.model.Price;
import no.ikov.paymentservice.domain.repository.PaymentRepository;
import no.ikov.paymentservice.infrastructure.dto.PaymentRequest;
import no.ikov.paymentservice.infrastructure.dto.PaymentResponse;
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
}
