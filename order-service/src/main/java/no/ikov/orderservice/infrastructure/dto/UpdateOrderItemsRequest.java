package no.ikov.orderservice.infrastructure.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class UpdateOrderItemsRequest {
    @NotEmpty @Valid
    private List<OrderItemRequest> items;
}
