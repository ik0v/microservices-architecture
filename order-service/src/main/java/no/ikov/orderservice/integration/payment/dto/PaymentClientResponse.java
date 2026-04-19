package no.ikov.orderservice.integration.payment.dto;

public record PaymentClientResponse(
        Long id,
        Long orderId,
        String status
) {}
