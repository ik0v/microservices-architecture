package no.ikov.orderservice.infrastructure.exceptions;

public class OrderAlreadyPaidException extends RuntimeException {

    public OrderAlreadyPaidException(Long orderId) {
        super("Order " + orderId + " already has a payment initiated");
    }
}
