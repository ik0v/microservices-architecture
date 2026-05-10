package no.ikov.paymentservice.application;

import lombok.RequiredArgsConstructor;
import no.ikov.paymentservice.domain.model.IdempotencyStatus;
import no.ikov.paymentservice.domain.repository.IdempotencyRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class IdempotencyCleanupService {

    // PENDING records older than this are assumed to be from crashed/timed-out requests
    private static final int PENDING_EXPIRY_SECONDS = 30;

    private final IdempotencyRepository idempotencyRepository;

    @Scheduled(fixedDelay = 60_000)
    public void removeStuckPendingKeys() {
        LocalDateTime threshold = LocalDateTime.now().minusSeconds(PENDING_EXPIRY_SECONDS);
        var stuckRecords = idempotencyRepository.findByStatusAndCreatedAtBefore(
                IdempotencyStatus.PENDING, threshold);
        idempotencyRepository.deleteAll(stuckRecords);
    }
}
