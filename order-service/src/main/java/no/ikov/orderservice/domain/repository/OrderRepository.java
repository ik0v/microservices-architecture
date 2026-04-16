package no.ikov.orderservice.domain.repository;

import no.ikov.orderservice.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
