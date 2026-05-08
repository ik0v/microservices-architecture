package no.ikov.paymentservice.infrastructure.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class LeakyBucketRateLimiter {

    private final long millisBetweenRequests;
    private final AtomicLong nextSlotAvailableAt;

    public LeakyBucketRateLimiter(@Value("${rate-limiter.leak-rate-per-second}") int leakRatePerSecond) {
        this.millisBetweenRequests = 1000L / leakRatePerSecond;
        // First slot is available right now — any request arriving after construction is immediately allowed
        this.nextSlotAvailableAt = new AtomicLong(System.currentTimeMillis());
    }

    public boolean tryAcquire() {
        long now = System.currentTimeMillis();
        long slotAvailableAt = nextSlotAvailableAt.get();

        // Next slot hasn't opened yet — bucket hasn't leaked, reject
        if (now < slotAvailableAt) {
            return false;
        }
        // Claim the slot: move the ticket forward to (now + interval).
        // If another thread already moved it between our read and now, this returns false.
        return nextSlotAvailableAt.compareAndSet(slotAvailableAt, now + millisBetweenRequests);
    }
}
