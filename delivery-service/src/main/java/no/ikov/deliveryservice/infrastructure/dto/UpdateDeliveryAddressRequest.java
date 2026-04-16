package no.ikov.deliveryservice.infrastructure.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateDeliveryAddressRequest {

    @NotNull @Valid
    private DeliveryAddressRequest deliveryAddress;
}
