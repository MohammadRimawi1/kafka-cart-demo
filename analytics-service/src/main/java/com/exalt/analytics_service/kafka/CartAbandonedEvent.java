package com.exalt.analytics_service.kafka;

import java.time.Instant;
import java.util.UUID;

/**
 * Mirrors cart-service's CartAbandonedEvent. This is a deliberately
 * separate copy -- analytics-service does not share Java classes with
 * cart-service across the process boundary. It only needs to agree on
 * the JSON shape published to the "cart-abandoned" topic.
 *
 * @author Mohammad Rimawi
 */
public class CartAbandonedEvent {
    private UUID cartId;
    private Instant lastUpdatedAt;
    private Instant detectedAt;

    /**
     * Default constructor, required by Jackson for deserialization.
     */
    public CartAbandonedEvent() { }

    public CartAbandonedEvent(UUID cartId, Instant lastUpdatedAt, Instant detectedAt) {
        this.cartId = cartId;
        this.lastUpdatedAt = lastUpdatedAt;
        this.detectedAt = detectedAt;
    }

//    ==== GETTERS ====
    /**
     * a method for getting the cart ID
     * @return
     */
    public UUID getCartId() { return cartId; }

    /**
     * a method for getting the last updated at timestamp
     * @return
     */
    public Instant getLastUpdatedAt() { return lastUpdatedAt; }

    /**
     * a method for getting the detected at timestamp
     * @return
     */
    public Instant getDetectedAt() { return detectedAt; }
//    ==== GETTERS ====
}