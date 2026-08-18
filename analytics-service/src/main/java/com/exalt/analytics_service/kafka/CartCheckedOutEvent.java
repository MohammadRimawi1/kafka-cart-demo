package com.exalt.analytics_service.kafka;

import java.time.Instant;
import java.util.UUID;

/**
 * Mirrors cart-service's CartCheckedOutEvent. This is a deliberately
 * separate copy -- analytics-service does not share Java classes with
 * cart-service across the process boundary. It only needs to agree on
 * the JSON shape published to the "cart-checked-out" topic.
 *
 * @author Mohammad Rimawi
 */
public class CartCheckedOutEvent {
    private UUID cartId;
    private int totalItems;
    private Instant timestamp;

    /**
     * Default Constructor
     */
    public CartCheckedOutEvent() { }

    public CartCheckedOutEvent(UUID cartId, int totalItems, Instant timestamp) {
        this.cartId = cartId;
        this.totalItems = totalItems;
        this.timestamp = timestamp;
    }

//    ==== GETTERS ====
    /**
     * a method for getting the cart ID
     * @return
     */
    public UUID getCartId() { return cartId; }

    /**
     * a method for getting the total items
     * @return
     */
    public int getTotalItems() { return totalItems; }

    /**
     * a method for getting the time stamps
     * @return
     */
    public Instant getTimestamp() { return timestamp; }
//    ==== GETTERS ====
}