package no.ikov.orderservice.infrastructure.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import no.ikov.orderservice.domain.model.PaymentMethod;

@Data
public class PayOrderRequest {

    @NotNull
    private PaymentMethod paymentMethod;
}
