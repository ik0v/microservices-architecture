package no.ikov.paymentservice.infrastructure;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import no.ikov.paymentservice.application.PaymentService;
import no.ikov.paymentservice.infrastructure.dto.PaymentRequest;
import no.ikov.paymentservice.infrastructure.dto.PaymentResponse;
import no.ikov.paymentservice.infrastructure.dto.UpdatePaymentStatusRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody @Valid PaymentRequest request) {
        PaymentResponse response = paymentService.createPayment(request);
        URI location = URI.create("/payments/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PaymentResponse> updateStatus(@PathVariable Long id, @RequestBody @Valid UpdatePaymentStatusRequest request) {
        return ResponseEntity.ok(paymentService.updateStatus(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
