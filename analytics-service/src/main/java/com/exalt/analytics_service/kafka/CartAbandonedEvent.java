package com.exalt.analytics_service.kafka;

import java.time.Instant;
import java.util.UUID;

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