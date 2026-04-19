package no.ikov.orderservice.infrastructure.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record UpdateOrderItemsRequest(
        @NotEmpty @Valid List<OrderItemRequest> items
) {}
