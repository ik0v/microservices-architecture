package no.ikov.paymentservice.infrastructure.exceptions;

public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(Long id) {
        super("Payment not found: " + id);
    }
}
