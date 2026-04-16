package no.ikov.orderservice.infrastructure.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;

@Data
public class PriceRequest {
    @NotNull @Positive
    private BigDecimal amount;
    @NotNull
    private Currency currency;
}
