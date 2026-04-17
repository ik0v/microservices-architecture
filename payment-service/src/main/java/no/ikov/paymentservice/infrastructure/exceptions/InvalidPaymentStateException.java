package no.ikov.paymentservice.infrastructure.exceptions;

public class InvalidPaymentStateException extends RuntimeException {

    public InvalidPaymentStateException(String message) {
        super(message);
    }
}
