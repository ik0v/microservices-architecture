package no.ikov.deliveryservice.infrastructure;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import no.ikov.deliveryservice.application.DeliveryService;
import no.ikov.deliveryservice.infrastructure.dto.DeliveryRequest;
import no.ikov.deliveryservice.infrastructure.dto.DeliveryResponse;
import no.ikov.deliveryservice.infrastructure.dto.UpdateCourierRequest;
import no.ikov.deliveryservice.infrastructure.dto.UpdateDeliveryAddressRequest;
import no.ikov.deliveryservice.infrastructure.dto.UpdateDeliveryStatusRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/deliveries")
@RequiredArgsConstructor
public class DeliveryController implements DeliveryControllerApi {

    private final DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<DeliveryResponse> createDelivery(@RequestBody @Valid DeliveryRequest request) {
        DeliveryResponse response = deliveryService.createDelivery(request);
        URI location = URI.create("/deliveries/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DeliveryResponse> updateStatus(@PathVariable Long id, @RequestBody @Valid UpdateDeliveryStatusRequest request) {
        return ResponseEntity.ok(deliveryService.updateStatus(id, request));
    }

    @PatchMapping("/{id}/courier")
    public ResponseEntity<DeliveryResponse> updateCourier(@PathVariable Long id, @RequestBody @Valid UpdateCourierRequest request) {
        return ResponseEntity.ok(deliveryService.updateCourier(id, request));
    }

    @PatchMapping("/{id}/address")
    public ResponseEntity<DeliveryResponse> updateAddress(@PathVariable Long id, @RequestBody @Valid UpdateDeliveryAddressRequest request) {
        return ResponseEntity.ok(deliveryService.updateAddress(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDelivery(@PathVariable Long id) {
        deliveryService.deleteDelivery(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponse> getDeliveryById(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.getDeliveryById(id));
    }

    @GetMapping
    public ResponseEntity<Page<DeliveryResponse>> getAllDeliveries(Pageable pageable) {
        return ResponseEntity.ok(deliveryService.getAllDeliveries(pageable));
    }
}
