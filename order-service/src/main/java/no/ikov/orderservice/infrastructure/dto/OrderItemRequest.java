package no.ikov.orderservice.infrastructure.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequest(
        @NotNull @Positive Long productId,
        @NotBlank String productName,
        @Positive int quantity,
        @NotNull @Valid PriceRequest unitPrice
) {}
