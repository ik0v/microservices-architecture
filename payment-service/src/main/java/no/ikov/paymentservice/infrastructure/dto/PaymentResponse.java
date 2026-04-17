package no.ikov.paymentservice.infrastructure.dto;

import lombok.Getter;
import no.ikov.paymentservice.domain.model.Payment;
import no.ikov.paymentservice.domain.model.PaymentMethod;
import no.ikov.paymentservice.domain.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

@Getter
public class PaymentResponse {

    private Long id;
    private Long orderId;
    private Long customerId;
    private PriceResponse price;
    private PaymentStatus status;
    private PaymentMethod method;
    private String transactionId;
    private LocalDateTime refundedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PaymentResponse from(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.id = payment.getId();
        response.orderId = payment.getOrderId();
        response.customerId = payment.getCustomerId();
        response.price = new PriceResponse(payment.getPrice().getAmount(), payment.getPrice().getCurrency());
        response.status = payment.getStatus();
        response.method = payment.getMethod();
        response.transactionId = payment.getTransactionId();
        response.refundedAt = payment.getRefundedAt();
        response.createdAt = payment.getCreatedAt();
        response.updatedAt = payment.getUpdatedAt();
        return response;
    }

    public record PriceResponse(BigDecimal amount, Currency currency) {}
}
