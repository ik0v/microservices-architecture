package no.ikov.paymentservice.integration.saga.ordercreation.event;

public enum OrderCreationStatus {
    ORDER_CREATED,
    PAYMENT_CONFIRMED,
    PAYMENT_FAILED,
    DELIVERY_CREATED,
    DELIVERY_FAILED,
    ORDER_COMPLETED
}
