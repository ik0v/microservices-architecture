package no.ikov.orderservice.infrastructure.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

@Schema(description = "Request to create a new order")
public record OrderRequest(
        @Schema(description = "ID of the customer placing the order", example = "1")
        @NotNull @Positive Long customerId,

        @Schema(description = "Delivery address for the order")
        @NotNull @Valid DeliveryAddressRequest deliveryAddress,

        @Schema(description = "List of items in the order")
        @NotEmpty @Valid List<OrderItemRequest> items
) {}
