package no.ikov.deliveryservice.infrastructure.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DeliveryAddressRequest {

    @NotBlank
    private String street;

    @NotBlank
    private String city;

    @NotBlank
    private String postalCode;

    @NotBlank
    private String country;
}
