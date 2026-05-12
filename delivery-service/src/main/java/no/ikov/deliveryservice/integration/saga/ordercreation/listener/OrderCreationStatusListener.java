package no.ikov.deliveryservice.integration.saga.ordercreation.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.ikov.deliveryservice.application.DeliveryService;
import no.ikov.deliveryservice.domain.model.Delivery;
import no.ikov.deliveryservice.domain.model.DeliveryAddress;
import no.ikov.deliveryservice.integration.saga.ordercreation.event.OrderCreationStatus;
import no.ikov.deliveryservice.integration.saga.ordercreation.event.OrderCreationStatusMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreationStatusListener {

    private final DeliveryService deliveryService;
    private final KafkaTemplate<String, OrderCreationStatusMessage> sagaKafkaTemplate;

    @Value("${kafka.topics.order-creation-status}")
    private String orderCreationStatusTopic;

    @KafkaListener(
            topics = "${kafka.topics.order-creation-status}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "sagaListenerContainerFactory"
    )
    public void consume(OrderCreationStatusMessage message, Acknowledgment ack) {
        if (message.status() == OrderCreationStatus.PAYMENT_CONFIRMED) {
            DeliveryAddress address = new DeliveryAddress(
                    message.street(), message.city(), message.postalCode(), message.country()
            );
            Delivery delivery = deliveryService.createDeliveryFromSaga(message.orderId(), address);
            log.info("Created delivery {} for order {}", delivery.getId(), message.orderId());
            publishDeliveryResult(message, delivery.getId(), true);
        }

        ack.acknowledge();
    }

    private void publishDeliveryResult(OrderCreationStatusMessage message, Long deliveryId, boolean success) {
        OrderCreationStatusMessage result = message.toBuilder()
                .deliveryId(success ? deliveryId : null)
                .status(success ? OrderCreationStatus.DELIVERY_CREATED : OrderCreationStatus.DELIVERY_FAILED)
                .build();
        sagaKafkaTemplate.send(orderCreationStatusTopic, result);
        log.info("Published {} for order {}", result.status(), message.orderId());
    }
}
