package com.exalt.cart_service.config;

/**
 * Central place for Kafka topic names used across cart-service.
 * Avoids hardcoding topic strings in multiple classes.
 *
 * @author Mohammad Rimawi
 */
public final class KafkaTopics {
    public static final String CART_CREATED = "cart-created";
    public static final String CART_CHECKED_OUT = "cart-checked-out";
    public static final String CART_ABANDONED = "cart-abandoned";

    private KafkaTopics() {
        // prevent instantiation, this class is just a holder for constants
    }
}