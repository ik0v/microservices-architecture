package no.ikov.orderservice.integration.payment.dto;

import java.math.BigDecimal;

public record PaymentClientResponse(
        Long id,
        Long orderId,
        Long customerId,
        PriceResponse price,
        String status,
        String method,
        String transactionId
) {
    public record PriceResponse(BigDecimal amount, String currency) {}
}
