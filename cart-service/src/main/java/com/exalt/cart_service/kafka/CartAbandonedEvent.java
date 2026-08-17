package com.exalt.cart_service.kafka;

import java.time.Instant;
import java.util.UUID;

/**
 * Event published to the "cart-abandoned" topic when the
 * scheduler detects a cart that hasn't been touched in a while.
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
}