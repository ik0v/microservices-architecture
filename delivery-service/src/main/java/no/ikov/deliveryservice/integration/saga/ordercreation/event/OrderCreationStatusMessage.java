package no.ikov.deliveryservice.integration.saga.ordercreation.event;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OrderCreationStatusMessage(
        UUID orderId,
        OrderCreationStatus status,
        BigDecimal amount,
        String currency,
        String street,
        String city,
        String postalCode,
        String country,
        UUID paymentId,
        UUID deliveryId
) {
}
