package no.ikov.paymentservice.integration.rabbitmq.config;

import no.ikov.paymentservice.infrastructure.dto.PaymentRequest;
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

    public static final String QUEUE = "payment-request-queue";
    public static final String PAYMENT_REQUEST_TYPE_ID = "payment-request";

    @Bean
    public Queue paymentRequestQueue() {
        return QueueBuilder.durable(QUEUE).build();
    }

    @Bean
    public DefaultClassMapper classMapper() {
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put(PAYMENT_REQUEST_TYPE_ID, PaymentRequest.class);

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
