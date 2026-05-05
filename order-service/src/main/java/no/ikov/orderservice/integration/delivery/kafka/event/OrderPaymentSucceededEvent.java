package no.ikov.orderservice.integration.delivery.kafka.event;

public record OrderPaymentSucceededEvent(
        Long orderId,
        DeliveryAddress deliveryAddress
) {
    public record DeliveryAddress(
            String street,
            String city,
            String postalCode,
            String country
    ) {}
}
