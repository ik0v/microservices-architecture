package no.ikov.orderservice.integration.delivery.kafka.config;

import org.springframework.kafka.support.serializer.JsonSerializer;
import no.ikov.orderservice.integration.delivery.kafka.event.OrderPaymentSucceededEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;

import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

@Configuration
public class KafkaDeliveryConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.topics.order-deliveries}")
    private String orderDeliveriesTopic;

    @Bean
    public KafkaTemplate<String, OrderPaymentSucceededEvent> kafkaTemplate() {
        Map<String, Object> configs = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        );
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configs));
    }

    @Bean
    public NewTopic orderDeliveriesTopic() {
        return TopicBuilder.name(orderDeliveriesTopic).build();
    }
}
