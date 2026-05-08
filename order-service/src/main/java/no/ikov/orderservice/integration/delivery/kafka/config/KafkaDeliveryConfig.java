package no.ikov.orderservice.integration.delivery.kafka.config;

import no.ikov.orderservice.integration.delivery.kafka.event.DeliveryCreatedEvent;
import no.ikov.orderservice.integration.delivery.kafka.event.OrderPaymentSucceededEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@EnableKafka
@Configuration
public class KafkaDeliveryConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${kafka.topics.order-deliveries}")
    private String orderDeliveriesTopic;

    @Bean
    public ConsumerFactory<String, DeliveryCreatedEvent> consumerFactory() {
        Map<String, Object> configs = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG, groupId,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                JsonDeserializer.TRUSTED_PACKAGES, "*",
                JsonDeserializer.VALUE_DEFAULT_TYPE, DeliveryCreatedEvent.class.getName(),
                JsonDeserializer.USE_TYPE_INFO_HEADERS, false
        );
        return new DefaultKafkaConsumerFactory<>(configs);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DeliveryCreatedEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, DeliveryCreatedEvent> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, DeliveryCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

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
