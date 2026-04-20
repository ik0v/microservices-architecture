package no.ikov.deliveryservice.infrastructure.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import no.ikov.deliveryservice.domain.model.DeliveryStatus;

@Getter
@Schema(description = "Request to update the status of a delivery")
public class UpdateDeliveryStatusRequest {

    @Schema(description = "New delivery status", example = "IN_TRANSIT")
    @NotNull
    private DeliveryStatus status;
}
