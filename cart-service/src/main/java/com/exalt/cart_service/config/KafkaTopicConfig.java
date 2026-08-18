package com.exalt.cart_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Declares the Kafka topics this service needs to exist.
 * Spring Boot auto-detects these NewTopic beans on startup and
 * creates the topics via its auto-configured KafkaAdmin.
 *
 * @author Mohammad Rimawi
 */
@Configuration
public class KafkaTopicConfig {

    /**
     * Declares the "cart-created" topic with 1 partition and a
     * replication factor of 1 -- fine for a single-broker local demo,
     * would need to be higher in a real multi-broker deployment for
     * fault tolerance.
     */
    @Bean
    public NewTopic cartCreatedTopic() {
        return new NewTopic(KafkaTopics.CART_CREATED, 1, (short) 1);
    }

    /**
     * Declares the "cart-checked-out" topic, same partition/replication
     * settings as above.
     */
    @Bean
    public NewTopic cartCheckedOutTopic() {
        return new NewTopic(KafkaTopics.CART_CHECKED_OUT, 1, (short) 1);
    }

    /**
     * Declares the "cart-abandoned" topic, same partition/replication
     * settings as above.
     */
    @Bean
    public NewTopic cartAbandonedTopic() {
        return new NewTopic(KafkaTopics.CART_ABANDONED, 1, (short) 1);
    }
}