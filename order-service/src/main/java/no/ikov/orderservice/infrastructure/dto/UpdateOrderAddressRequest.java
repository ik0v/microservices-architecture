package no.ikov.orderservice.infrastructure.dto;

import lombok.Data;

@Data
public class UpdateOrderAddressRequest {
    private DeliveryAddressRequest deliveryAddress;
}
