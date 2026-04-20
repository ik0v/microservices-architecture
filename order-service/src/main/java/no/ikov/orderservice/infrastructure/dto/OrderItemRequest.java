package no.ikov.orderservice.infrastructure.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "A single item in the order")
public record OrderItemRequest(
        @Schema(description = "Product ID", example = "42")
        @NotNull @Positive Long productId,

        @Schema(description = "Product name", example = "Wireless Keyboard")
        @NotBlank String productName,

        @Schema(description = "Quantity ordered", example = "2")
        @Positive int quantity,

        @Schema(description = "Unit price of the product")
        @NotNull @Valid PriceRequest unitPrice
) {}
