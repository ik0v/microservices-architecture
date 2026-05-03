package no.ikov.paymentservice.integration.rabbitmq.listener;

import lombok.RequiredArgsConstructor;
import no.ikov.paymentservice.application.PaymentService;
import no.ikov.paymentservice.integration.rabbitmq.config.RabbitMQPaymentConfig;
import no.ikov.paymentservice.infrastructure.dto.PaymentRequest;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentRequestListener {

    private final PaymentService paymentService;

    @RabbitListener(queues = RabbitMQPaymentConfig.QUEUE)
    @Transactional
    public void handlePaymentRequest(PaymentRequest request) {
        paymentService.createPayment(request);
    }
}
