package no.ikov.orderservice.integration.payment.rabbitmq.listener;

import lombok.RequiredArgsConstructor;
import no.ikov.orderservice.domain.model.Order;
import no.ikov.orderservice.domain.model.OrderStatus;
import no.ikov.orderservice.domain.repository.OrderRepository;
import no.ikov.orderservice.integration.payment.rabbitmq.config.RabbitMQPaymentConfig;
import no.ikov.orderservice.integration.payment.dto.PaymentClientResponse;
import no.ikov.orderservice.infrastructure.exceptions.OrderNotFoundException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentResultListener {

    private final OrderRepository orderRepository;

    @RabbitListener(queues = RabbitMQPaymentConfig.RESULT_QUEUE)
    @Transactional
    public void handlePaymentResult(PaymentClientResponse response) {
        Order order = orderRepository.findById(response.orderId())
                .orElseThrow(() -> new OrderNotFoundException(response.orderId()));
        if ("COMPLETED".equals(response.status())) {
            order.assignPayment(response.id());
            order.transitionTo(OrderStatus.CONFIRMED);
        } else {
            order.transitionTo(OrderStatus.CANCELLED);
        }
        orderRepository.save(order);
    }
}
