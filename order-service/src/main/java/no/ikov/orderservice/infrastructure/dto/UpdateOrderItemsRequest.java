package no.ikov.orderservice.infrastructure.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "Request to replace all items in an order")
public record UpdateOrderItemsRequest(
        @Schema(description = "New list of order items (replaces existing)")
        @NotEmpty @Valid List<OrderItemRequest> items
) {}
