package no.ikov.orderservice.infrastructure.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;

@Data
public class PriceRequest {
    private BigDecimal amount;
    private Currency currency;
}
