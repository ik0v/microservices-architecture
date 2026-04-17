package no.ikov.paymentservice.infrastructure.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import no.ikov.paymentservice.domain.model.PaymentMethod;

@Getter
public class PaymentRequest {

    @NotNull @Positive
    private Long orderId;

    @NotNull @Positive
    private Long customerId;

    @NotNull @Valid
    private PriceRequest price;

    @NotNull
    private PaymentMethod method;
}
