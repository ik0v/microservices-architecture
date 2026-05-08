package no.ikov.deliveryservice.integration.order.kafka.event;

public record DeliveryCreatedEvent(
        Long deliveryId,
        Long orderId
) {}
