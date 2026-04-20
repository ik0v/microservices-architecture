package no.ikov.orderservice.infrastructure.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import no.ikov.orderservice.domain.model.OrderStatus;

@Schema(description = "Request to update the status of an order")
public record UpdateOrderStatusRequest(
        @Schema(description = "New order status", example = "IN_DELIVERY")
        @NotNull OrderStatus status
) {}
