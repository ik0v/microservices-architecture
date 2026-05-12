package no.ikov.deliveryservice.application;

import lombok.RequiredArgsConstructor;
import no.ikov.deliveryservice.domain.model.Delivery;
import no.ikov.deliveryservice.domain.model.DeliveryAddress;
import no.ikov.deliveryservice.domain.model.DeliveryStatus;
import no.ikov.deliveryservice.domain.repository.DeliveryRepository;
import no.ikov.deliveryservice.domain.model.Courier;
import no.ikov.deliveryservice.infrastructure.dto.DeliveryRequest;
import no.ikov.deliveryservice.infrastructure.dto.DeliveryResponse;
import no.ikov.deliveryservice.infrastructure.dto.UpdateCourierRequest;
import no.ikov.deliveryservice.infrastructure.dto.UpdateDeliveryAddressRequest;
import no.ikov.deliveryservice.infrastructure.dto.UpdateDeliveryStatusRequest;
import no.ikov.deliveryservice.infrastructure.exceptions.DeliveryNotFoundException;
import no.ikov.deliveryservice.infrastructure.exceptions.InvalidDeliveryStateException;

import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    @Transactional
    public DeliveryResponse createDeliveryFromOrder(Long orderId, DeliveryAddress address) {
        Delivery delivery = new Delivery(orderId, address, LocalDateTime.now().plusDays(3));
        return DeliveryResponse.from(deliveryRepository.save(delivery));
    }

    @Transactional
    public DeliveryResponse createDelivery(DeliveryRequest request) {
        Delivery delivery = new Delivery(
                request.getOrderId(),
                new DeliveryAddress(
                        request.getDeliveryAddress().getStreet(),
                        request.getDeliveryAddress().getCity(),
                        request.getDeliveryAddress().getPostalCode(),
                        request.getDeliveryAddress().getCountry()
                ),
                request.getEstimatedDeliveryAt()
        );
        return DeliveryResponse.from(deliveryRepository.save(delivery));
    }

    @Transactional
    public DeliveryResponse updateStatus(Long id, UpdateDeliveryStatusRequest request) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new DeliveryNotFoundException(id));
        delivery.transitionTo(request.getStatus());
        return DeliveryResponse.from(deliveryRepository.save(delivery));
    }

    @Transactional
    public DeliveryResponse updateCourier(Long id, UpdateCourierRequest request) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new DeliveryNotFoundException(id));

        Set<DeliveryStatus> allowedStatuses = Set.of(DeliveryStatus.PENDING, DeliveryStatus.ASSIGNED);
        if (!allowedStatuses.contains(delivery.getStatus())) {
            throw new InvalidDeliveryStateException(
                    "Courier can only be assigned when status is PENDING or ASSIGNED"
            );
        }

        delivery.setCourier(new Courier(request.getCourierId(), request.getName(), request.getPhone()));
        return DeliveryResponse.from(deliveryRepository.save(delivery));
    }

    @Transactional
    public DeliveryResponse updateAddress(Long id, UpdateDeliveryAddressRequest request) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new DeliveryNotFoundException(id));

        Set<DeliveryStatus> allowedStatuses = Set.of(DeliveryStatus.PENDING, DeliveryStatus.ASSIGNED);
        if (!allowedStatuses.contains(delivery.getStatus())) {
            throw new InvalidDeliveryStateException(
                    "Delivery address can only be updated when status is PENDING or ASSIGNED"
            );
        }

        delivery.setDeliveryAddress(new DeliveryAddress(
                request.getDeliveryAddress().getStreet(),
                request.getDeliveryAddress().getCity(),
                request.getDeliveryAddress().getPostalCode(),
                request.getDeliveryAddress().getCountry()
        ));
        return DeliveryResponse.from(deliveryRepository.save(delivery));
    }

    @Transactional(readOnly = true)
    public DeliveryResponse getDeliveryById(Long id) {
        return deliveryRepository.findById(id)
                .map(DeliveryResponse::from)
                .orElseThrow(() -> new DeliveryNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Page<DeliveryResponse> getAllDeliveries(Pageable pageable) {
        return deliveryRepository.findAll(pageable).map(DeliveryResponse::from);
    }

    @Transactional
    public Delivery createDeliveryFromSaga(Long orderId, DeliveryAddress address) {
        Delivery delivery = new Delivery(orderId, address, LocalDateTime.now().plusDays(3));
        return deliveryRepository.save(delivery);
    }

    @Transactional
    public void deleteDelivery(Long id) {
        if (!deliveryRepository.existsById(id)) {
            throw new DeliveryNotFoundException(id);
        }
        deliveryRepository.deleteById(id);
    }
}
