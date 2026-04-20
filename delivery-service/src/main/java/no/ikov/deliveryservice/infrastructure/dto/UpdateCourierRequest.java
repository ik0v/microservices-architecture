package no.ikov.deliveryservice.infrastructure.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
@Schema(description = "Request to assign or update the courier for a delivery")
public class UpdateCourierRequest {

    @Schema(description = "Courier ID", example = "7")
    @NotNull @Positive
    private Long courierId;

    @Schema(description = "Courier full name", example = "Erik Hansen")
    @NotBlank
    private String name;

    @Schema(description = "Courier phone number", example = "+47 912 34 567")
    @NotBlank
    private String phone;
}
