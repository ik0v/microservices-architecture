package no.ikov.deliveryservice.infrastructure.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Schema(description = "Request to update the delivery address")
public class UpdateDeliveryAddressRequest {

    @Schema(description = "New delivery address")
    @NotNull @Valid
    private DeliveryAddressRequest deliveryAddress;
}
