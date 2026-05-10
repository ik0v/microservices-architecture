package no.ikov.paymentservice.integration.order.rabbitmq.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.ikov.paymentservice.application.PaymentService;
import no.ikov.paymentservice.infrastructure.dto.PaymentRequest;
import no.ikov.paymentservice.integration.order.rabbitmq.config.RabbitMQPaymentConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRequestListener {

    private final PaymentService paymentService;

    @RabbitListener(queues = RabbitMQPaymentConfig.QUEUE)
    public void handlePaymentRequest(PaymentRequest request) {
        try {
            paymentService.processPaymentRequest(request);
        } catch (DataIntegrityViolationException e) {
            // Payment for this order already exists — duplicate delivery, safe to discard
            log.warn("Duplicate payment request for orderId [{}] — skipping", request.getOrderId());
        }
    }
}