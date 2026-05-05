package no.ikov.deliveryservice.integration.order.kafka.config;

import no.ikov.deliveryservice.integration.order.kafka.event.DeliveryCreatedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Configuration
public class KafkaOrderConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.topics.delivery-created}")
    private String deliveryCreatedTopic;

    @Bean
    public KafkaTemplate<String, DeliveryCreatedEvent> kafkaTemplate() {
        Map<String, Object> configs = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        );
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configs));
    }

    @Bean
    public NewTopic deliveryCreatedTopic() {
        return TopicBuilder.name(deliveryCreatedTopic).build();
    }
}
