package no.ikov.deliveryservice.infrastructure.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DeliveryRequest {

    @NotNull @Positive
    private Long orderId;

    @NotNull @Valid
    private DeliveryAddressRequest deliveryAddress;

    @NotNull @Future
    private LocalDateTime estimatedDeliveryAt;
}
