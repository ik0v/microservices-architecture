package no.ikov.orderservice.infrastructure.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private Long customerId;
    private DeliveryAddressRequest deliveryAddress;
    private List<OrderItemRequest> items;
}
