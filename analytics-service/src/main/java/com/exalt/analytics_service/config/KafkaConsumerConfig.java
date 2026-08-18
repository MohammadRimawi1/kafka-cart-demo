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

/**
 * Defines one ConsumerFactory + ListenerContainerFactory pair per event type
 * consumed by analytics-service.
 *
 * Why this exists: analytics-service listens to three topics
 * (cart-created, cart-checked-out, cart-abandoned), and each one carries a
 * differently-shaped event. Spring Boot's automatic Kafka configuration only
 * supports one global deserializer target class per service, which works
 * fine for a single-topic consumer (like notification-service) but breaks
 * down the moment more than one event shape is involved -- there's no single
 * class to default to. This config solves that by manually building a
 * dedicated factory per event type, each one explicitly told which class to
 * deserialize into. Each @KafkaListener method then points at the matching
 * factory via its containerFactory attribute.
 *
 * @author Mohammad Rimawi
 */
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Settings shared by every consumer in this service, regardless of
     * which event type it reads: where Kafka lives, which consumer group
     * to report under, where to start reading if no offset has been
     * committed yet, and how to deserialize the message key (always a
     * plain String -- the cart's UUID -- across all three topics).
     */
    private Map<String, Object> baseConsumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "analytics-service-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    // ==================== cart-created ====================

    /**
     * Builds the raw consumer connection for cart-created messages.
     * The JacksonJsonDeserializer here is fixed to CartCreatedEvent,
     * so every message read through this factory is guaranteed to be
     * converted into that exact type.
     *
     * setUseTypeHeaders(false) tells the deserializer to ignore the
     * producer's embedded class-name header entirely, since that header
     * points at cart-service's own package (com.exalt.cart_service...),
     * which does not exist inside analytics-service. We supply our own
     * target class instead of trusting that header.
     *
     * addTrustedPackages("*") is Jackson's security allowlist for which
     * packages it's permitted to instantiate classes from during
     * deserialization. "*" removes the restriction entirely -- acceptable
     * for a local learning project, not something to leave wide open in
     * a production system.
     */
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

    /**
     * Wraps cartCreatedConsumerFactory with the actual listening machinery:
     * the polling loop that continuously checks Kafka for new messages, and
     * the ack mode. MANUAL means Kafka will not auto-commit offsets on a
     * timer -- the listener method must explicitly call ack.acknowledge()
     * before a message is considered processed.
     *
     * This bean's method name ("cartCreatedListenerFactory") is what gets
     * referenced by name in @KafkaListener(containerFactory = "...") on the
     * matching listener method.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CartCreatedEvent> cartCreatedListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CartCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cartCreatedConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    // ==================== cart-checked-out ====================

    /**
     * Same pattern as cartCreatedConsumerFactory, fixed to
     * CartCheckedOutEvent instead.
     */
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

    /**
     * Same pattern as cartCreatedListenerFactory, wrapping
     * cartCheckedOutConsumerFactory instead.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CartCheckedOutEvent> cartCheckedOutListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CartCheckedOutEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cartCheckedOutConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    // ==================== cart-abandoned ====================

    /**
     * Same pattern again, fixed to CartAbandonedEvent.
     */
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

    /**
     * Same pattern again, wrapping cartAbandonedConsumerFactory.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CartAbandonedEvent> cartAbandonedListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CartAbandonedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cartAbandonedConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }
}