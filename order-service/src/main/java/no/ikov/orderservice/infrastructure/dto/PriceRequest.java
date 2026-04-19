package no.ikov.orderservice.infrastructure.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Currency;

public record PriceRequest(
        @NotNull @Positive BigDecimal amount,
        @NotNull Currency currency
) {}
