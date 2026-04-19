package no.ikov.orderservice.infrastructure.dto;

import jakarta.validation.constraints.NotBlank;

public record DeliveryAddressRequest(
        @NotBlank String street,
        @NotBlank String city,
        @NotBlank String postalCode,
        @NotBlank String country
) {}
