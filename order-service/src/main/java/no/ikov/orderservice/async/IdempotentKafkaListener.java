package no.ikov.orderservice.async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.ikov.orderservice.async.entity.AsyncMessage;
import no.ikov.orderservice.async.entity.AsyncMessageStatus;
import no.ikov.orderservice.async.entity.AsyncMessageType;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.support.Acknowledgment;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public abstract class IdempotentKafkaListener<T> {

    protected final AsyncMessageRepo asyncMessageRepo;
    protected final JsonMapper jsonMapper;

    protected void process(ConsumerRecord<String, T> record, Acknowledgment acknowledgment) {
        String key = extractIdempotencyKey(record);

        AsyncMessage consumedMessage = AsyncMessage.builder()
                .id(key)
                .topic(record.topic())
                .type(AsyncMessageType.INBOX)
                .status(AsyncMessageStatus.RECEIVED)
                .value(jsonMapper.writeValueAsString(record.value()))
                .build();

        try {
            asyncMessageRepo.save(consumedMessage);
        } catch (DataIntegrityViolationException e) {
            log.warn("Duplicate message skipped — key: {}, topic: {}", key, record.topic());
            acknowledgment.acknowledge();
            return;
        }

        handleMessage(record.value());

        consumedMessage.setStatus(AsyncMessageStatus.PROCESSED);
        asyncMessageRepo.save(consumedMessage);
        acknowledgment.acknowledge();
    }

    protected abstract void handleMessage(T payload);

    private String extractIdempotencyKey(ConsumerRecord<String, T> record) {
        Header header = record.headers().lastHeader("X-Idempotency-Key");
        if (header == null) {
            throw new IllegalArgumentException("Missing X-Idempotency-Key header on topic: " + record.topic());
        }
        return new String(header.value(), StandardCharsets.UTF_8);
    }
}
