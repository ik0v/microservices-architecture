package no.ikov.orderservice.async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.ikov.orderservice.async.entity.AsyncMessage;
import no.ikov.orderservice.async.entity.AsyncMessageStatus;
import no.ikov.orderservice.infrastructure.exceptions.OutboxMessageException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxProcessor {

    private final AsyncMessageRepo repo;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public void sendMessage(AsyncMessage message) {
        try {
            kafkaTemplate.send(message.getTopic(), message.getId().getId(), message.getValue())
                    .exceptionally(e -> {
                        throw new OutboxMessageException("Error sending outbox message '%s'".formatted(message.getId()), e);
                    })
                    .get();
            message.setStatus(AsyncMessageStatus.SENT);
            repo.save(message);
        } catch (Exception e) {
            throw new OutboxMessageException("Error processing outbox message '%s'".formatted(message.getId()), e);
        }
    }
}
