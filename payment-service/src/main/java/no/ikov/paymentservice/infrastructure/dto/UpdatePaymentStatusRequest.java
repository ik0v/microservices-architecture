package no.ikov.paymentservice.infrastructure.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import no.ikov.paymentservice.domain.model.PaymentStatus;

@Getter
public class UpdatePaymentStatusRequest {

    @NotNull
    private PaymentStatus status;

    // Required only when status is COMPLETED
    private String transactionId;
}
