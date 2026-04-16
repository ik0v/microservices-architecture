package no.ikov.deliveryservice.application;

import lombok.RequiredArgsConstructor;
import no.ikov.deliveryservice.domain.model.Delivery;
import no.ikov.deliveryservice.domain.model.DeliveryAddress;
import no.ikov.deliveryservice.domain.model.DeliveryStatus;
import no.ikov.deliveryservice.domain.repository.DeliveryRepository;
import no.ikov.deliveryservice.infrastructure.dto.DeliveryRequest;
import no.ikov.deliveryservice.infrastructure.dto.DeliveryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    @Transactional
    public DeliveryResponse createDelivery(DeliveryRequest request) {
        Delivery delivery = new Delivery();
        delivery.setOrderId(request.getOrderId());
        delivery.setStatus(DeliveryStatus.PENDING);
        delivery.setDeliveryAddress(new DeliveryAddress(
                request.getDeliveryAddress().getStreet(),
                request.getDeliveryAddress().getCity(),
                request.getDeliveryAddress().getPostalCode(),
                request.getDeliveryAddress().getCountry()
        ));
        delivery.setEstimatedDeliveryAt(request.getEstimatedDeliveryAt());

        return DeliveryResponse.from(deliveryRepository.save(delivery));
    }
}
