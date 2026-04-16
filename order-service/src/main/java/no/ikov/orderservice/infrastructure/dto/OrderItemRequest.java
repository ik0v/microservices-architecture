package no.ikov.orderservice.infrastructure.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderItemRequest {
    @NotNull @Positive
    private Long productId;
    @NotBlank
    private String productName;
    @Positive
    private int quantity;
    @NotNull @Valid
    private PriceRequest unitPrice;
}
