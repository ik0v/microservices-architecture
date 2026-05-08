package no.ikov.orderservice.async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.ikov.orderservice.async.entity.AsyncMessage;
import no.ikov.orderservice.async.entity.AsyncMessageStatus;
import no.ikov.orderservice.async.entity.AsyncMessageType;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private static final int BATCH_SIZE = 50;

    private final AsyncMessageRepo repo;
    private final OutboxProcessor processor;

    @Scheduled(fixedDelay = 3000)
    public void sendOutboxMessages() {
        List<AsyncMessage> messages = repo.findByTypeAndStatus(
                AsyncMessageType.OUTBOX,
                AsyncMessageStatus.CREATED,
                Pageable.ofSize(BATCH_SIZE)
        );
        if (messages.isEmpty()) {
            return;
        }
        log.info("Outbox relay: found {} pending message(s)", messages.size());
        for (AsyncMessage message : messages) {
            processor.sendMessage(message);
        }
    }
}
