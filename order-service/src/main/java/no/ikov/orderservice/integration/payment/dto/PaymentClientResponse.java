package no.ikov.orderservice.integration.payment.dto;

import lombok.Getter;

@Getter
public class PaymentClientResponse {

    private Long id;
    private Long orderId;
    private String status;
}