package no.ikov.paymentservice.infrastructure.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import no.ikov.paymentservice.domain.model.PaymentMethod;

@Getter
@Schema(description = "Request to create a new payment")
public class PaymentRequest {

    @Schema(description = "ID of the order being paid", example = "1")
    @NotNull @Positive
    private Long orderId;

    @Schema(description = "ID of the customer making the payment", example = "1")
    @NotNull @Positive
    private Long customerId;

    @Schema(description = "Amount and currency to charge")
    @NotNull @Valid
    private PriceRequest price;

    @Schema(description = "Payment method to use", example = "CREDIT_CARD")
    @NotNull
    private PaymentMethod method;
}
