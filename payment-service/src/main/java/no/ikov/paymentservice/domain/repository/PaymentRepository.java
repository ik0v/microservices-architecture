package no.ikov.paymentservice.domain.repository;

import no.ikov.paymentservice.domain.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
