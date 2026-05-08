package no.ikov.paymentservice.integration.order.rabbitmq.config;

import no.ikov.paymentservice.infrastructure.dto.PaymentRequest;
import no.ikov.paymentservice.infrastructure.dto.PaymentResponse;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQPaymentConfig {

    public static final String QUEUE = "payment-request-queue";
    public static final String PAYMENT_REQUEST_TYPE_ID = "payment-request";

    public static final String RESULT_EXCHANGE = "payment-result-exchange";
    public static final String RESULT_ROUTING_KEY = "payment-result-queue";
    public static final String PAYMENT_RESULT_TYPE_ID = "payment-result";

    // Dead-letter exchange and queue — messages nacked by the listener land here
    // instead of being requeued indefinitely (e.g. DB down during processing)
    public static final String DEAD_LETTER_EXCHANGE = "payment-dlx";
    public static final String DEAD_LETTER_QUEUE = "payment-request-dead";

    @Bean
    public Queue paymentRequestQueue() {
        return QueueBuilder.durable(QUEUE)
                // On nack/rejection, forward to the DLX rather than requeue
                .withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DEAD_LETTER_QUEUE)
                .build();
    }

    // DLX routes rejected messages to the dead-letter queue
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    @Bean
    public Queue paymentDeadLetterQueue() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    @Bean
    public Binding deadLetterBinding(Queue paymentDeadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(paymentDeadLetterQueue).to(deadLetterExchange).with(DEAD_LETTER_QUEUE);
    }

    @Bean
    public DirectExchange paymentResultExchange() {
        return new DirectExchange(RESULT_EXCHANGE);
    }

    // Override the default listener factory to set prefetch.
    // Default prefetch is 250 — too high for payment processing which involves
    // DB writes and a RabbitMQ publish per message. 5 keeps throughput reasonable
    // without starving other consumers or overwhelming the service under load.
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        factory.setPrefetchCount(5);
        return factory;
    }

    @Bean
    public DefaultClassMapper classMapper() {
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put(PAYMENT_REQUEST_TYPE_ID, PaymentRequest.class);
        idClassMapping.put(PAYMENT_RESULT_TYPE_ID, PaymentResponse.class);

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
