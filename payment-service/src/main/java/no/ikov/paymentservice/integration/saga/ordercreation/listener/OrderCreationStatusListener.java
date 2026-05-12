package no.ikov.paymentservice.integration.saga.ordercreation.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.ikov.paymentservice.application.PaymentService;
import no.ikov.paymentservice.domain.model.Payment;
import no.ikov.paymentservice.domain.model.PaymentStatus;
import no.ikov.paymentservice.integration.saga.ordercreation.event.OrderCreationStatus;
import no.ikov.paymentservice.integration.saga.ordercreation.event.OrderCreationStatusMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreationStatusListener {

    private final PaymentService paymentService;
    private final KafkaTemplate<String, OrderCreationStatusMessage> sagaKafkaTemplate;

    @Value("${kafka.topics.order-creation-status}")
    private String orderCreationStatusTopic;

    @KafkaListener(
            topics = "${kafka.topics.order-creation-status}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "sagaListenerContainerFactory"
    )
    public void consume(OrderCreationStatusMessage message, Acknowledgment ack) {
        OrderCreationStatus status = message.status();

        if (status == OrderCreationStatus.ORDER_CREATED) {
            Payment payment = paymentService.createAndProcessPayment(
                    message.orderId(), message.customerId(),
                    message.amount(), message.currency(), message.paymentMethod()
            );
            boolean success = payment.getStatus() == PaymentStatus.COMPLETED;
            log.info("Processed payment for order {}: {}", message.orderId(), payment.getStatus());
            publishPaymentResult(message, payment.getId(), success);

        } else if (status == OrderCreationStatus.DELIVERY_FAILED) {
            paymentService.refundByOrderId(message.orderId());
            log.info("Refunded payment for order {} due to DELIVERY_FAILED", message.orderId());
        }

        ack.acknowledge();
    }

    private void publishPaymentResult(OrderCreationStatusMessage message, Long paymentId, boolean success) {
        OrderCreationStatusMessage result = message.toBuilder()
                .paymentId(success ? paymentId : null)
                .status(success ? OrderCreationStatus.PAYMENT_CONFIRMED : OrderCreationStatus.PAYMENT_FAILED)
                .build();
        sagaKafkaTemplate.send(orderCreationStatusTopic, result);
        log.info("Published {} for order {}", result.status(), message.orderId());
    }
}
