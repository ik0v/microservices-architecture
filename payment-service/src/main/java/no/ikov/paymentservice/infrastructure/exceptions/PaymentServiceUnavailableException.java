package no.ikov.paymentservice.infrastructure.exceptions;

public class PaymentServiceUnavailableException extends RuntimeException {
    public PaymentServiceUnavailableException(String message) {
        super(message);
    }
}
