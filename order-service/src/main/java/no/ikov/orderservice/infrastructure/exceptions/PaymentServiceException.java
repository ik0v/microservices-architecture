package no.ikov.orderservice.infrastructure.exceptions;

public class PaymentServiceException extends RuntimeException {

    public PaymentServiceException(String message) {
        super(message);
    }
}
