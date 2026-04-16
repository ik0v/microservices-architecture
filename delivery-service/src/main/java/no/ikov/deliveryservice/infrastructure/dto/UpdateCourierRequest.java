package no.ikov.deliveryservice.infrastructure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class UpdateCourierRequest {

    @NotNull @Positive
    private Long courierId;

    @NotBlank
    private String name;

    @NotBlank
    private String phone;
}
