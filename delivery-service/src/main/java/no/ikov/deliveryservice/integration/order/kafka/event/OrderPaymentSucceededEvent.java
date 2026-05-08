package no.ikov.deliveryservice.integration.order.kafka.event;

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
