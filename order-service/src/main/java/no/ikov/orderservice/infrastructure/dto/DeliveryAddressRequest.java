package no.ikov.orderservice.infrastructure.dto;

import lombok.Data;

@Data
public class DeliveryAddressRequest {
    private String street;
    private String city;
    private String postalCode;
    private String country;
}
