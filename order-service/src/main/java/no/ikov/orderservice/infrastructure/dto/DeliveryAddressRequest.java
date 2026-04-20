package no.ikov.orderservice.infrastructure.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Delivery address")
public record DeliveryAddressRequest(
        @Schema(description = "Street and house number", example = "Karl Johans gate 1")
        @NotBlank String street,

        @Schema(description = "City", example = "Oslo")
        @NotBlank String city,

        @Schema(description = "Postal code", example = "0154")
        @NotBlank String postalCode,

        @Schema(description = "Country", example = "Norway")
        @NotBlank String country
) {}
