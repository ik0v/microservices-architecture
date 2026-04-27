package no.ikov.orderservice.config;

import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ResilienceConfig {

    private final RetryRegistry retryRegistry;
    private final BulkheadRegistry bulkheadRegistry;

    @PostConstruct
    public void configureRetryLogging() {
        retryRegistry.retry("paymentService").getEventPublisher()
                .onRetry(event -> log.warn(
                        "[Retry] Attempt #{} for '{}' after: {}",
                        event.getNumberOfRetryAttempts(),
                        event.getName(),
                        event.getLastThrowable().getClass().getSimpleName()
                ))
                .onError(event -> log.error(
                        "[Retry] All {} attempts exhausted for '{}'. Last error: {}",
                        event.getNumberOfRetryAttempts(),
                        event.getName(),
                        event.getLastThrowable().getMessage()
                ))
                .onSuccess(event -> {
                    if (event.getNumberOfRetryAttempts() > 0) {
                        log.info(
                                "[Retry] '{}' succeeded after {} retries",
                                event.getName(),
                                event.getNumberOfRetryAttempts()
                        );
                    }
                });
    }

    @PostConstruct
    public void configureBulkheadLogging() {
        bulkheadRegistry.bulkhead("paymentService").getEventPublisher()
                .onCallRejected(event -> log.warn(
                        "[Bulkhead] Call rejected for '{}' — all {} slots occupied",
                        event.getBulkheadName(),
                        bulkheadRegistry.bulkhead("paymentService").getBulkheadConfig().getMaxConcurrentCalls()
                ))
                .onCallFinished(event -> log.debug(
                        "[Bulkhead] Call finished for '{}'",
                        event.getBulkheadName()
                ));
    }
}
