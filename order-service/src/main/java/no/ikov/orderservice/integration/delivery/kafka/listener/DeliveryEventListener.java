package no.ikov.orderservice.integration.delivery.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import no.ikov.orderservice.async.AsyncMessageRepo;
import no.ikov.orderservice.async.IdempotentKafkaListener;
import no.ikov.orderservice.domain.repository.OrderRepository;
import no.ikov.orderservice.infrastructure.exceptions.OrderNotFoundException;
import no.ikov.orderservice.integration.delivery.kafka.event.DeliveryCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
public class DeliveryEventListener extends IdempotentKafkaListener<DeliveryCreatedEvent> {

    private final OrderRepository orderRepository;

    public DeliveryEventListener(AsyncMessageRepo asyncMessageRepo, JsonMapper jsonMapper, OrderRepository orderRepository) {
        super(asyncMessageRepo, jsonMapper);
        this.orderRepository = orderRepository;
    }

    @KafkaListener(
            topics = "${kafka.topics.delivery-created}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void onMessage(ConsumerRecord<String, DeliveryCreatedEvent> record, Acknowledgment acknowledgment) {
        process(record, acknowledgment);
    }

    @Override
    protected void handleMessage(DeliveryCreatedEvent event) {
        log.info("Received delivery created event: {}", event);
        var order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new OrderNotFoundException(event.orderId()));
        order.assignDelivery(event.deliveryId());
        orderRepository.save(order);
    }
}
