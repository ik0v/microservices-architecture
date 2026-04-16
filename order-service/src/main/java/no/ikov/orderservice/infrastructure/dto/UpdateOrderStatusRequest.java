package no.ikov.orderservice.infrastructure.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import no.ikov.orderservice.domain.model.OrderStatus;

@Data
public class UpdateOrderStatusRequest {
    @NotNull
    private OrderStatus status;
}
