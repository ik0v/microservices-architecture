package no.ikov.orderservice.infrastructure.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    @NotNull @Positive
    private Long customerId;
    @NotNull @Valid
    private DeliveryAddressRequest deliveryAddress;
    @NotEmpty @Valid
    private List<OrderItemRequest> items;
}
