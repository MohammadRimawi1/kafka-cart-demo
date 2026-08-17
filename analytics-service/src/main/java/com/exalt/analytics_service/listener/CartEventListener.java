package com.exalt.analytics_service.listener;

import com.exalt.analytics_service.kafka.CartAbandonedEvent;
import com.exalt.analytics_service.kafka.CartCheckedOutEvent;
import com.exalt.analytics_service.kafka.CartCreatedEvent;
import com.exalt.analytics_service.service.AnalyticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * Listens to all three cart topics, in a single consumer group
 * ("analytics-service-group"), and increments the corresponding
 * counter in AnalyticsService for each event received.
 *
 * Each method is wired to its own listener container factory
 * (see KafkaConsumerConfig) since each topic carries a different
 * event shape.
 *
 * @author Mohammad Rimawi
 */
@Component
public class CartEventListener {

    private static final Logger log = LoggerFactory.getLogger(CartEventListener.class);

    private final AnalyticsService analyticsService;

    public CartEventListener(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @KafkaListener(
            topics = "cart-created",
            groupId = "analytics-service-group",
            containerFactory = "cartCreatedListenerFactory"
    )
    public void handleCartCreated(CartCreatedEvent event, Acknowledgment ack) {
        analyticsService.incrementCartCreated();
        log.info("Counted cart-created for cart {}", event.getCartId());
        ack.acknowledge();
    }

    @KafkaListener(
            topics = "cart-checked-out",
            groupId = "analytics-service-group",
            containerFactory = "cartCheckedOutListenerFactory"
    )
    public void handleCartCheckedOut(CartCheckedOutEvent event, Acknowledgment ack) {
        analyticsService.incrementCartCheckedOut();
        log.info("Counted cart-checked-out for cart {}", event.getCartId());
        ack.acknowledge();
    }

    @KafkaListener(
            topics = "cart-abandoned",
            groupId = "analytics-service-group",
            containerFactory = "cartAbandonedListenerFactory"
    )
    public void handleCartAbandoned(CartAbandonedEvent event, Acknowledgment ack) {
        analyticsService.incrementCartAbandoned();
        log.info("Counted cart-abandoned for cart {}", event.getCartId());
        ack.acknowledge();
    }
}