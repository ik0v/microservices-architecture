package no.ikov.orderservice.infrastructure.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to update the delivery address of an order")
public record UpdateOrderAddressRequest(
        @Schema(description = "New delivery address")
        @NotNull @Valid DeliveryAddressRequest deliveryAddress
) {}
