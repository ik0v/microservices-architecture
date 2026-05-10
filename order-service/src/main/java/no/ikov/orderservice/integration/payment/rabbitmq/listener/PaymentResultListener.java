package no.ikov.orderservice.integration.payment.rabbitmq.listener;

import lombok.RequiredArgsConstructor;
import no.ikov.orderservice.application.OrderService;
import no.ikov.orderservice.integration.payment.rabbitmq.config.RabbitMQPaymentConfig;
import no.ikov.orderservice.integration.payment.dto.PaymentClientResponse;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentResultListener {

    private final OrderService orderService;

    @RabbitListener(queues = RabbitMQPaymentConfig.RESULT_QUEUE)
    public void handlePaymentResult(PaymentClientResponse response) {
        if ("COMPLETED".equals(response.status())) {
            orderService.confirmPayment(response.orderId(), response.id());
        } else {
            orderService.cancelPayment(response.orderId());
        }
    }
}
