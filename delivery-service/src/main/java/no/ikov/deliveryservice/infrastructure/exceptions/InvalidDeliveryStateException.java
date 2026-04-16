package no.ikov.deliveryservice.infrastructure.exceptions;

public class InvalidDeliveryStateException extends RuntimeException {

    public InvalidDeliveryStateException(String message) {
        super(message);
    }
}
