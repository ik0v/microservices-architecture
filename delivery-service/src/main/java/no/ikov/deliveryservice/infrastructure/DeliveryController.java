package no.ikov.deliveryservice.infrastructure;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import no.ikov.deliveryservice.application.DeliveryService;
import no.ikov.deliveryservice.infrastructure.dto.DeliveryRequest;
import no.ikov.deliveryservice.infrastructure.dto.DeliveryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<DeliveryResponse> createDelivery(@RequestBody @Valid DeliveryRequest request) {
        DeliveryResponse response = deliveryService.createDelivery(request);
        URI location = URI.create("/deliveries/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }
}
