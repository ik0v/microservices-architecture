package no.ikov.deliveryservice.domain.repository;

import no.ikov.deliveryservice.domain.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
