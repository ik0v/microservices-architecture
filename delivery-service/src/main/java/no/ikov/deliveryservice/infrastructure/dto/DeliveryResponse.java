package no.ikov.deliveryservice.infrastructure.dto;

import lombok.Getter;
import no.ikov.deliveryservice.domain.model.Delivery;
import no.ikov.deliveryservice.domain.model.DeliveryStatus;

import java.time.LocalDateTime;

@Getter
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
        response.id = delivery.getId();
        response.orderId = delivery.getOrderId();
        response.status = delivery.getStatus();
        response.estimatedDeliveryAt = delivery.getEstimatedDeliveryAt();
        response.actualDeliveryAt = delivery.getActualDeliveryAt();
        response.createdAt = delivery.getCreatedAt();
        response.updatedAt = delivery.getUpdatedAt();

        response.deliveryAddress = new DeliveryAddressResponse(
                delivery.getDeliveryAddress().getStreet(),
                delivery.getDeliveryAddress().getCity(),
                delivery.getDeliveryAddress().getPostalCode(),
                delivery.getDeliveryAddress().getCountry()
        );

        if (delivery.getCourier() != null) {
            response.courier = new CourierResponse(
                    delivery.getCourier().getCourierId(),
                    delivery.getCourier().getName(),
                    delivery.getCourier().getPhone()
            );
        }

        return response;
    }

    public record DeliveryAddressResponse(String street, String city, String postalCode, String country) {}

    public record CourierResponse(Long courierId, String name, String phone) {}
}
