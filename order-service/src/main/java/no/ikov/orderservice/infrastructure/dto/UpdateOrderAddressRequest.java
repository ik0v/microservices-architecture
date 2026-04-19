package no.ikov.orderservice.infrastructure.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderAddressRequest(
        @NotNull @Valid DeliveryAddressRequest deliveryAddress
) {}
