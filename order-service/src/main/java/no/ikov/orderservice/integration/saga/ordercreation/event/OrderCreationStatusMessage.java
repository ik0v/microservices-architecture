package no.ikov.orderservice.integration.saga.ordercreation.event;

import lombok.Builder;

import java.math.BigDecimal;

@Builder(toBuilder = true)
public record OrderCreationStatusMessage(
        Long orderId,
        Long customerId,
        OrderCreationStatus status,
        BigDecimal amount,
        String currency,
        String paymentMethod,
        String street,
        String city,
        String postalCode,
        String country,
        Long paymentId,
        Long deliveryId
) {
}
