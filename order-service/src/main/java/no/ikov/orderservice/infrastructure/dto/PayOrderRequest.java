package no.ikov.orderservice.infrastructure.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import no.ikov.orderservice.domain.model.PaymentMethod;

@Schema(description = "Request to pay for an order")
public record PayOrderRequest(
        @Schema(description = "Payment method to use", example = "CREDIT_CARD")
        @NotNull PaymentMethod paymentMethod
) {}
