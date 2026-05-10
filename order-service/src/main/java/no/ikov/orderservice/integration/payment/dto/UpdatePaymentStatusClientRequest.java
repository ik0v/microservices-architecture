package no.ikov.orderservice.integration.payment.dto;

public record UpdatePaymentStatusClientRequest(String status, String transactionId) {
}
