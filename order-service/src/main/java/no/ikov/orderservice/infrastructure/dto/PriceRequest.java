package no.ikov.orderservice.infrastructure.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Currency;

@Schema(description = "Monetary amount with currency")
public record PriceRequest(
        @Schema(description = "Amount", example = "299.99")
        @NotNull @Positive BigDecimal amount,

        @Schema(description = "ISO 4217 currency code", example = "NOK")
        @NotNull Currency currency
) {}
