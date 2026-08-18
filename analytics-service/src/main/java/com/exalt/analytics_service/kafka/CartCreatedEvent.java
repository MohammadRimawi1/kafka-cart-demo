package com.exalt.analytics_service.kafka;

import java.time.Instant;
import java.util.UUID;

/**
 * Mirrors cart-service's CartCreatedEvent. This is a deliberately
 * separate copy -- analytics-service does not share Java classes with
 * cart-service across the process boundary. It only needs to agree on
 * the JSON shape published to the "cart-created" topic.
 *
 * @author Mohammad Rimawi
 */
public class CartCreatedEvent {
    private UUID cartId;
    private int productId;
    private int quantity;
    private Instant timestamp;

    /**
     * Default Constructor
     */
    public CartCreatedEvent() { }

    public CartCreatedEvent(UUID cartId, int productId, int quantity, Instant timestamp) {
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

//    ==== GETTERS ====
    /**
     * a method for getting the cart ID
     * @return
     */
    public UUID getCartId() { return cartId; }

    /**
     * a method for getting the product ID
     * @return
     */
    public int getProductId() { return productId; }

    /**
     * a method for getting the quantity
     * @return
     */
    public int getQuantity() { return quantity; }

    /**
     * a method for getting the time stamp
     * @return
     */
    public Instant getTimestamp() { return timestamp; }
//    ==== GETTERS ====
}