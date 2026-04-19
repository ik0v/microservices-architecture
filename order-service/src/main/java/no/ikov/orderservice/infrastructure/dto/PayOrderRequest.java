package no.ikov.orderservice.infrastructure.dto;

import jakarta.validation.constraints.NotNull;
import no.ikov.orderservice.domain.model.PaymentMethod;

public record PayOrderRequest(
        @NotNull PaymentMethod paymentMethod
) {}
