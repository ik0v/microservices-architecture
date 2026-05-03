package no.ikov.orderservice.integration.payment.config;

import no.ikov.orderservice.integration.payment.dto.PaymentClientRequest;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQPaymentConfig {

    public static final String EXCHANGE = "payment-request-exchange";
    public static final String QUEUE = "payment-request-queue";
    public static final String ROUTING_KEY = "payment-request-queue";
    public static final String PAYMENT_REQUEST_TYPE_ID = "payment-request";

    @Bean
    public Queue paymentRequestQueue() {
        return QueueBuilder.durable(QUEUE).build();
    }

    @Bean
    public DirectExchange paymentRequestExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding paymentRequestBinding(Queue paymentRequestQueue, DirectExchange paymentRequestExchange) {
        return BindingBuilder.bind(paymentRequestQueue).to(paymentRequestExchange).with(ROUTING_KEY);
    }

    @Bean
    public DefaultClassMapper classMapper() {
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put(PAYMENT_REQUEST_TYPE_ID, PaymentClientRequest.class);

        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setIdClassMapping(idClassMapping);
        return classMapper;
    }

    @Bean
    public MessageConverter jsonMessageConverter(DefaultClassMapper classMapper) {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        converter.setClassMapper(classMapper);
        return converter;
    }

}
