package no.ikov.orderservice.integration.saga.ordercreation.config;

import no.ikov.orderservice.integration.saga.ordercreation.event.OrderCreationStatusMessage;
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
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@EnableKafka
@Configuration
public class KafkaSagaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${kafka.topics.order-creation-status}")
    private String orderCreationStatusTopic;

    @Bean
    public ConsumerFactory<String, OrderCreationStatusMessage> sagaConsumerFactory() {
        Map<String, Object> configs = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG, groupId,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                JsonDeserializer.TRUSTED_PACKAGES, "*",
                JsonDeserializer.VALUE_DEFAULT_TYPE, OrderCreationStatusMessage.class.getName(),
                JsonDeserializer.USE_TYPE_INFO_HEADERS, false
        );
        return new DefaultKafkaConsumerFactory<>(configs);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderCreationStatusMessage> sagaListenerContainerFactory(
            ConsumerFactory<String, OrderCreationStatusMessage> sagaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, OrderCreationStatusMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(sagaConsumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    @Bean
    public KafkaTemplate<String, OrderCreationStatusMessage> sagaKafkaTemplate() {
        Map<String, Object> configs = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        );
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configs));
    }

    @Bean
    public KafkaTemplate<String, String> outboxKafkaTemplate() {
        Map<String, Object> configs = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class
        );
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configs));
    }

    @Bean
    public NewTopic orderCreationStatusTopic() {
        return TopicBuilder.name(orderCreationStatusTopic).build();
    }
}
