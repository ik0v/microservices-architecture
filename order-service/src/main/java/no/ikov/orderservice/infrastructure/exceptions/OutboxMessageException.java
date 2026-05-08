package no.ikov.orderservice.infrastructure.exceptions;

public class OutboxMessageException extends RuntimeException {

    public OutboxMessageException(String message) {
        super(message);
    }

    public OutboxMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}