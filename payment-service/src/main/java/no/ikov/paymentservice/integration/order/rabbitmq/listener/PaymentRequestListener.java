package no.ikov.paymentservice.integration.order.rabbitmq.listener;

import lombok.RequiredArgsConstructor;
import no.ikov.paymentservice.application.PaymentServiceRabbitMQ;
import no.ikov.paymentservice.domain.model.PaymentStatus;
import no.ikov.paymentservice.infrastructure.dto.PaymentRequest;
import no.ikov.paymentservice.infrastructure.dto.PaymentResponse;
import no.ikov.paymentservice.infrastructure.dto.UpdatePaymentStatusRequest;
import no.ikov.paymentservice.integration.order.rabbitmq.config.RabbitMQPaymentConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class PaymentRequestListener {

    private final PaymentServiceRabbitMQ paymentService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQPaymentConfig.QUEUE)
    @Transactional
    public void handlePaymentRequest(PaymentRequest request) {
        PaymentResponse created = paymentService.createPayment(request);

        PaymentResponse result;
        if (ThreadLocalRandom.current().nextInt(100) < 70) {
            result = paymentService.updateStatus(
                    created.getId(),
                    new UpdatePaymentStatusRequest(PaymentStatus.COMPLETED, UUID.randomUUID().toString())
            );
        } else {
            result = paymentService.updateStatus(
                    created.getId(),
                    new UpdatePaymentStatusRequest(PaymentStatus.FAILED, null)
            );
        }

        rabbitTemplate.convertAndSend(
                RabbitMQPaymentConfig.RESULT_EXCHANGE,
                RabbitMQPaymentConfig.RESULT_ROUTING_KEY,
                result
        );
    }
}
