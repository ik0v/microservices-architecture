package no.ikov.deliveryservice.infrastructure.exceptions;

public class DeliveryNotFoundException extends RuntimeException {

    public DeliveryNotFoundException(Long id) {
        super("Delivery not found: " + id);
    }
}
