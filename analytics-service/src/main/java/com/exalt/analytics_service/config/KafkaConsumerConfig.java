package com.exalt.analytics_service.config;

import com.exalt.analytics_service.kafka.CartAbandonedEvent;
import com.exalt.analytics_service.kafka.CartCheckedOutEvent;
import com.exalt.analytics_service.kafka.CartCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> baseConsumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "analytics-service-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    // ---- cart-created ----

    @Bean
    public ConsumerFactory<String, CartCreatedEvent> cartCreatedConsumerFactory() {
        JacksonJsonDeserializer<CartCreatedEvent> deserializer =
                new JacksonJsonDeserializer<>(CartCreatedEvent.class, false);
        deserializer.setUseTypeHeaders(false);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                baseConsumerProps(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CartCreatedEvent> cartCreatedListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CartCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cartCreatedConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    // ---- cart-checked-out ----

    @Bean
    public ConsumerFactory<String, CartCheckedOutEvent> cartCheckedOutConsumerFactory() {
        JacksonJsonDeserializer<CartCheckedOutEvent> deserializer =
                new JacksonJsonDeserializer<>(CartCheckedOutEvent.class, false);
        deserializer.setUseTypeHeaders(false);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                baseConsumerProps(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CartCheckedOutEvent> cartCheckedOutListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CartCheckedOutEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cartCheckedOutConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    // ---- cart-abandoned ----

    @Bean
    public ConsumerFactory<String, CartAbandonedEvent> cartAbandonedConsumerFactory() {
        JacksonJsonDeserializer<CartAbandonedEvent> deserializer =
                new JacksonJsonDeserializer<>(CartAbandonedEvent.class, false);
        deserializer.setUseTypeHeaders(false);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                baseConsumerProps(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CartAbandonedEvent> cartAbandonedListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CartAbandonedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cartAbandonedConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }
}