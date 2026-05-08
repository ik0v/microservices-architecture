package no.ikov.paymentservice.integration.order.rabbitmq.listener;

import lombok.RequiredArgsConstructor;
import no.ikov.paymentservice.application.PaymentService;
import no.ikov.paymentservice.infrastructure.dto.PaymentRequest;
import no.ikov.paymentservice.integration.order.rabbitmq.config.RabbitMQPaymentConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentRequestListener {

    private final PaymentService paymentService;

    @RabbitListener(queues = RabbitMQPaymentConfig.QUEUE)
    public void handlePaymentRequest(PaymentRequest request) {
        paymentService.processPaymentRequest(request);
    }
}