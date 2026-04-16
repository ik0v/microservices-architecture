package no.ikov.orderservice.infrastructure.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateOrderItemsRequest {
    private List<OrderItemRequest> items;
}
