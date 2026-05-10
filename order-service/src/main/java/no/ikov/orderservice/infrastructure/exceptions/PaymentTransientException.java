package no.ikov.orderservice.infrastructure.exceptions;

public class PaymentTransientException extends RuntimeException {

    public PaymentTransientException(String message) {
        super(message);
    }
}
