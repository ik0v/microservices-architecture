package no.ikov.orderservice.infrastructure.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record OrderRequest(
        @NotNull @Positive Long customerId,
        @NotNull @Valid DeliveryAddressRequest deliveryAddress,
        @NotEmpty @Valid List<OrderItemRequest> items
) {}
