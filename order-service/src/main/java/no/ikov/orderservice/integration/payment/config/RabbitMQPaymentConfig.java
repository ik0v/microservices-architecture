package no.ikov.orderservice.integration.payment.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQPaymentConfig {

    @Bean
    public Queue paymentRequestQueue() {
        return QueueBuilder.durable("payment-request-queue").build();
    }

    @Bean
    public DirectExchange paymentRequestExchange() {
        return new DirectExchange("payment-request-exchange");
    }

    @Bean
    public Binding paymentRequestBinding(Queue paymentRequestQueue, DirectExchange paymentRequestExchange) {
        return BindingBuilder.bind(paymentRequestQueue).to(paymentRequestExchange)
                .with("payment-request-queue");
    }

}
