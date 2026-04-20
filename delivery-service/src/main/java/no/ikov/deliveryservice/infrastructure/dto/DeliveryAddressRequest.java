package no.ikov.deliveryservice.infrastructure.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "Delivery address")
public class DeliveryAddressRequest {

    @Schema(description = "Street and house number", example = "Karl Johans gate 1")
    @NotBlank
    private String street;

    @Schema(description = "City", example = "Oslo")
    @NotBlank
    private String city;

    @Schema(description = "Postal code", example = "0154")
    @NotBlank
    private String postalCode;

    @Schema(description = "Country", example = "Norway")
    @NotBlank
    private String country;
}
