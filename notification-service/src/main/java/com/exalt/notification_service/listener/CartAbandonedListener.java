package com.exalt.notification_service.listener;

import com.exalt.notification_service.kafka.CartAbandonedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * Listens to the "cart-abandoned" topic and logs a fake reminder
 * message whenever an abandoned cart event shows up.
 *
 * This is the entire responsibility of notification-service:
 * react to one specific event type, do something lightweight,
 * done. It has no idea cart-service or analytics-service exist.
 *
 * @author Mohammad Rimawi
 */
@Component
public class CartAbandonedListener {

    private static final Logger log = LoggerFactory.getLogger(CartAbandonedListener.class);

    /**
     * @param event the deserialized CartAbandonedEvent payload
     * @param ack   manual acknowledgment handle -- calling ack.acknowledge()
     *              tells Kafka "I have successfully processed this message,
     *              move my committed offset past it." If we never call it,
     *              or the app crashes before calling it, this message will
     *              be redelivered on restart -- that's the safety guarantee
     *              manual ack buys you over auto-commit.
     */
    @KafkaListener(topics = "cart-abandoned", groupId = "notification-service-group")
    public void handleCartAbandoned(CartAbandonedEvent event, Acknowledgment ack) {
        log.info("Reminder: your cart {} has been sitting untouched since {} — come back and finish checking out!",
                event.getCartId(), event.getLastUpdatedAt());

        ack.acknowledge();
    }
}