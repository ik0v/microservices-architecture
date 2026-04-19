package no.ikov.orderservice.integration.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class PaymentClientRequest {

    private Long orderId;
    private Long customerId;
    private PriceRequest price;
    private String method;

    @Getter
    @AllArgsConstructor
    public static class PriceRequest {
        private BigDecimal amount;
        private String currency;
    }
}