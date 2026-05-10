package no.ikov.orderservice.integration.delivery.kafka.listener;

import lombok.RequiredArgsConstructor;
import no.ikov.orderservice.domain.repository.OrderRepository;
import no.ikov.orderservice.infrastructure.exceptions.OrderNotFoundException;
import no.ikov.orderservice.integration.delivery.kafka.event.DeliveryCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DeliveryEventListener {

    private final OrderRepository orderRepository;

    @KafkaListener(
            topics = "${kafka.topics.delivery-created}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void handleDeliveryCreated(DeliveryCreatedEvent event) {
        var order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new OrderNotFoundException(event.orderId()));
        order.assignDelivery(event.deliveryId());
        orderRepository.save(order);
    }
}
