package no.ikov.deliveryservice.infrastructure.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import no.ikov.deliveryservice.domain.model.DeliveryStatus;

@Getter
public class UpdateDeliveryStatusRequest {

    @NotNull
    private DeliveryStatus status;
}
