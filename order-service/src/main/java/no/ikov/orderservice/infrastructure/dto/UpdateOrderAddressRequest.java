package no.ikov.orderservice.infrastructure.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderAddressRequest {
    @NotNull @Valid
    private DeliveryAddressRequest deliveryAddress;
}
