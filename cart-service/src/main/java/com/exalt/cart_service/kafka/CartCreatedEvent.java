package com.exalt.cart_service.kafka;

import java.time.Instant;
import java.util.UUID;

/**
 * Event published to the "cart-created" topic whenever an item
 * is added to a cart.
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
    public UUID getCartId() { return cartId; }
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public Instant getTimestamp() { return timestamp; }
//    ==== GETTERS ====
}