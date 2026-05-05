package no.ikov.deliveryservice.integration.order.kafka.listener;

import lombok.RequiredArgsConstructor;
import no.ikov.deliveryservice.application.DeliveryService;
import no.ikov.deliveryservice.domain.model.DeliveryAddress;
import no.ikov.deliveryservice.infrastructure.dto.DeliveryResponse;
import no.ikov.deliveryservice.integration.order.kafka.event.DeliveryCreatedEvent;
import no.ikov.deliveryservice.integration.order.kafka.event.OrderPaymentSucceededEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final DeliveryService deliveryService;
    private final KafkaTemplate<String, DeliveryCreatedEvent> kafkaTemplate;

    @Value("${kafka.topics.delivery-created}")
    private String deliveryCreatedTopic;

    @KafkaListener(
            topics = "${kafka.topics.order-deliveries}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleOrderPaymentSucceeded(OrderPaymentSucceededEvent event) {
        DeliveryResponse delivery = deliveryService.createDeliveryFromOrder(
                event.orderId(),
                new DeliveryAddress(
                        event.deliveryAddress().street(),
                        event.deliveryAddress().city(),
                        event.deliveryAddress().postalCode(),
                        event.deliveryAddress().country()
                )
        );
        kafkaTemplate.send(
                deliveryCreatedTopic,
                String.valueOf(delivery.getId()),
                new DeliveryCreatedEvent(delivery.getId(), event.orderId())
        );
    }
}
