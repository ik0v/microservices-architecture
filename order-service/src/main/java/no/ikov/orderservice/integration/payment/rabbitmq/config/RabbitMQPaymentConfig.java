package no.ikov.orderservice.integration.payment.rabbitmq.config;

import no.ikov.orderservice.integration.payment.dto.PaymentClientRequest;
import no.ikov.orderservice.integration.payment.dto.PaymentClientResponse;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
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

    public static final String RESULT_EXCHANGE = "payment-result-exchange";
    public static final String RESULT_QUEUE = "payment-result-queue";
    public static final String RESULT_ROUTING_KEY = "payment-result-queue";
    public static final String PAYMENT_RESULT_TYPE_ID = "payment-result";

    // order-service is the producer for payment-request-queue — the queue itself
    // is owned and declared by payment-service (which also sets DLX arguments).
    // We only declare the exchange and a binding using the queue name directly,
    // so RabbitAdmin does not attempt to redeclare the queue without DLX args.
    @Bean
    public DirectExchange paymentRequestExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding paymentRequestBinding(DirectExchange paymentRequestExchange) {
        return new Binding(QUEUE, Binding.DestinationType.QUEUE, EXCHANGE, ROUTING_KEY, null);
    }

    @Bean
    public Queue paymentResultQueue() {
        return QueueBuilder.durable(RESULT_QUEUE).build();
    }

    @Bean
    public DirectExchange paymentResultExchange() {
        return new DirectExchange(RESULT_EXCHANGE);
    }

    @Bean
    public Binding paymentResultBinding(Queue paymentResultQueue, DirectExchange paymentResultExchange) {
        return BindingBuilder.bind(paymentResultQueue).to(paymentResultExchange).with(RESULT_ROUTING_KEY);
    }

    @Bean
    public DefaultClassMapper classMapper() {
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put(PAYMENT_REQUEST_TYPE_ID, PaymentClientRequest.class);
        idClassMapping.put(PAYMENT_RESULT_TYPE_ID, PaymentClientResponse.class);

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
