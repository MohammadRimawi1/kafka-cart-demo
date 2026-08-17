package com.exalt.notification_service.kafka;

import java.time.Instant;
import java.util.UUID;

/**
 * Mirrors cart-service's CartAbandonedEvent. This is a deliberately
 * separate copy -- notification-service does not share Java classes
 * with cart-service across the process boundary. It only needs to
 * agree on the JSON shape published to the "cart-abandoned" topic.
 *
 * @author Mohammad Rimawi
 */
public class CartAbandonedEvent {
    private UUID cartId;
    private Instant lastUpdatedAt;
    private Instant detectedAt;

    public CartAbandonedEvent() { }

    public CartAbandonedEvent(UUID cartId, Instant lastUpdatedAt, Instant detectedAt) {
        this.cartId = cartId;
        this.lastUpdatedAt = lastUpdatedAt;
        this.detectedAt = detectedAt;
    }

    public UUID getCartId() { return cartId; }
    public Instant getLastUpdatedAt() { return lastUpdatedAt; }
    public Instant getDetectedAt() { return detectedAt; }

    @Override
    public String toString() {
        return "CartAbandonedEvent{" +
                "cartId=" + cartId +
                ", lastUpdatedAt=" + lastUpdatedAt +
                ", detectedAt=" + detectedAt +
                '}';
    }
}