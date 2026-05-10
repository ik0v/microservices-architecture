package no.ikov.paymentservice.infrastructure.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import no.ikov.paymentservice.domain.model.PaymentStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request to update the status of a payment")
public class UpdatePaymentStatusRequest {

    @Schema(description = "New payment status", example = "COMPLETED")
    @NotNull
    private PaymentStatus status;

    @Schema(description = "External transaction ID — required when status is COMPLETED", example = "txn_abc123")
    private String transactionId;
}
