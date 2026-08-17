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

    @Bean
    public NewTopic cartCreatedTopic() {
        return new NewTopic(KafkaTopics.CART_CREATED, 1, (short) 1);
    }

    @Bean
    public NewTopic cartCheckedOutTopic() {
        return new NewTopic(KafkaTopics.CART_CHECKED_OUT, 1, (short) 1);
    }

    @Bean
    public NewTopic cartAbandonedTopic() {
        return new NewTopic(KafkaTopics.CART_ABANDONED, 1, (short) 1);
    }
}