package no.ikov.deliveryservice.infrastructure.dto;

import lombok.Getter;
import lombok.Setter;
import no.ikov.deliveryservice.domain.model.Delivery;
import no.ikov.deliveryservice.domain.model.DeliveryStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class DeliveryResponse {

    private Long id;
    private Long orderId;
    private DeliveryStatus status;
    private DeliveryAddressResponse deliveryAddress;
    private CourierResponse courier;
    private LocalDateTime estimatedDeliveryAt;
    private LocalDateTime actualDeliveryAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DeliveryResponse from(Delivery delivery) {
        DeliveryResponse response = new DeliveryResponse();
        response.setId(delivery.getId());
        response.setOrderId(delivery.getOrderId());
        response.setStatus(delivery.getStatus());
        response.setEstimatedDeliveryAt(delivery.getEstimatedDeliveryAt());
        response.setActualDeliveryAt(delivery.getActualDeliveryAt());
        response.setCreatedAt(delivery.getCreatedAt());
        response.setUpdatedAt(delivery.getUpdatedAt());

        response.setDeliveryAddress(new DeliveryAddressResponse(
                delivery.getDeliveryAddress().getStreet(),
                delivery.getDeliveryAddress().getCity(),
                delivery.getDeliveryAddress().getPostalCode(),
                delivery.getDeliveryAddress().getCountry()
        ));

        if (delivery.getCourier() != null) {
            response.setCourier(new CourierResponse(
                    delivery.getCourier().getCourierId(),
                    delivery.getCourier().getName(),
                    delivery.getCourier().getPhone()
            ));
        }

        return response;
    }

    public record DeliveryAddressResponse(String street, String city, String postalCode, String country) {}

    public record CourierResponse(Long courierId, String name, String phone) {}
}
