package no.ikov.orderservice.integration.saga.ordercreation.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.ikov.orderservice.application.OrderService;
import no.ikov.orderservice.integration.saga.ordercreation.event.OrderCreationStatus;
import no.ikov.orderservice.integration.saga.ordercreation.event.OrderCreationStatusMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreationStatusListener {

    private static final EnumSet<OrderCreationStatus> FAILURE_STATUSES =
            EnumSet.of(OrderCreationStatus.PAYMENT_FAILED, OrderCreationStatus.DELIVERY_FAILED);

    private final OrderService orderService;

    @KafkaListener(
            topics = "${kafka.topics.order-creation-status}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "sagaListenerContainerFactory"
    )
    public void consume(OrderCreationStatusMessage message, Acknowledgment ack) {
        OrderCreationStatus status = message.status();

        if (status == OrderCreationStatus.PAYMENT_CONFIRMED) {
            orderService.onPaymentConfirmed(message.orderId(), message.paymentId());
            log.info("Payment confirmed for order {}, assigned paymentId {}", message.orderId(), message.paymentId());

        } else if (status == OrderCreationStatus.DELIVERY_CREATED) {
            orderService.onDeliveryCreated(message.orderId(), message.deliveryId());
            log.info("Delivery created for order {}, assigned deliveryId {}", message.orderId(), message.deliveryId());

        } else if (FAILURE_STATUSES.contains(status)) {
            orderService.cancelOrderFromSaga(message.orderId());
            log.info("Order {} cancelled due to status {}", message.orderId(), status);
        }

        ack.acknowledge();
    }
}
