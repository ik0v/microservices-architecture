package no.ikov.paymentservice.infrastructure.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Schema(description = "Monetary amount with currency")
public class PriceRequest {

    @Schema(description = "Amount to charge", example = "299.99")
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @Schema(description = "ISO 4217 currency code", example = "NOK")
    @NotBlank
    @Size(min = 3, max = 3)
    private String currency;
}
