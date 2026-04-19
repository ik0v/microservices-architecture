package no.ikov.orderservice.integration.payment.dto;

import java.math.BigDecimal;

public record PaymentClientRequest(
        Long orderId,
        Long customerId,
        PriceRequest price,
        String method
) {
    public record PriceRequest(
            BigDecimal amount,
            String currency
    ) {}
}
