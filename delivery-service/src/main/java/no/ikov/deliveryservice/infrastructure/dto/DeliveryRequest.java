package no.ikov.deliveryservice.infrastructure.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Schema(description = "Request to create a new delivery")
public class DeliveryRequest {

    @Schema(description = "ID of the order this delivery belongs to", example = "1")
    @NotNull @Positive
    private Long orderId;

    @Schema(description = "Delivery address")
    @NotNull @Valid
    private DeliveryAddressRequest deliveryAddress;

    @Schema(description = "Estimated delivery date and time (must be in the future)", example = "2026-05-01T14:00:00")
    @NotNull @Future
    private LocalDateTime estimatedDeliveryAt;
}
