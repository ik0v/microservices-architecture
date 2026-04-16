package no.ikov.orderservice.infrastructure.dto;

import lombok.Data;
import no.ikov.orderservice.domain.model.OrderStatus;

@Data
public class UpdateOrderStatusRequest {
    private OrderStatus status;
}
