package no.ikov.orderservice.domain.model;

public enum OrderStatus {
    CREATED,
    PAYMENT_PENDING,
    CONFIRMED,
    IN_DELIVERY,
    DELIVERED,
    CANCELLED
}