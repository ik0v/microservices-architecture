package no.ikov.orderservice.infrastructure.dto;

import jakarta.validation.constraints.NotNull;
import no.ikov.orderservice.domain.model.OrderStatus;

public record UpdateOrderStatusRequest(
        @NotNull OrderStatus status
) {}
