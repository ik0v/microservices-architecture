package no.ikov.orderservice.integration.delivery.kafka.event;

public record DeliveryCreatedEvent(
        Long deliveryId,
        Long orderId
) {}
