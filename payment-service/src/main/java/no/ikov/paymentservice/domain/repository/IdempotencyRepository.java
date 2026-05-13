package no.ikov.paymentservice.domain.repository;

import no.ikov.paymentservice.domain.model.IdempotencyRecord;
import no.ikov.paymentservice.domain.model.IdempotencyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IdempotencyRepository extends JpaRepository<IdempotencyRecord, Long> {
    Optional<IdempotencyRecord> findByIdempotencyKey(String idempotencyKey);

    // Used by cleanup job to find requests that started but never completed
    List<IdempotencyRecord> findByStatusAndCreatedAtBefore(IdempotencyStatus status, LocalDateTime threshold);
}
