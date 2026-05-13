package no.ikov.paymentservice.domain.repository;

import no.ikov.paymentservice.domain.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {

    Optional<Payment> findByOrderId(Long orderId);
}
