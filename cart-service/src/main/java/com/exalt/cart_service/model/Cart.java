package com.exalt.cart_service.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Mohammad Rimawi
 */
public class Cart {
    private UUID cartId;
    private List<CartItem> items;
    private Instant lastUpdatedAt;
    private boolean checkedOut;
    private boolean abandonedEventSent;

    /**
     * non-parameterized constructor
     * generates a random id
     * initializes a list for the items
     * initializes an Instant time
     */
    public Cart() {
        this.cartId = UUID.randomUUID();
        this.items = new ArrayList<>();
        this.lastUpdatedAt = Instant.now();
    }

//    ==== GETTERS ====
    /**
     * a method for getting the cart Id
     * @return
     */
    public UUID getCartId() {
        return cartId;
    }

    /**
     * a method for getting the items
     * @return
     */
    public List<CartItem> getItems() {
        return items;
    }

    /**
     * a method for getting the last updated at time
     * @return
     */
    public Instant getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    /**
     * a method for getting if its checked out
     * @return
     */
    public boolean isCheckedOut() {
        return checkedOut;
    }

    /**
     * a method for getting if its abandoned event is sent
     * @return
     */
    public boolean isAbandonedEventSent() {
        return abandonedEventSent;
    }
//    ==== GETTERS ====

//    ==== SETTERS ====
    /**
     * a method for setting checked out
     */
    public void checkout() {
        this.checkedOut = true;
    }

    /**
     * a method for setting if its abandoned event
     * @param abandonedEventSent
     */
    public void setAbandonedEventSent(boolean abandonedEventSent) {
        this.abandonedEventSent = abandonedEventSent;
    }
//    ==== SETTERS ====

    public void addItem(CartItem item) {
        this.items.add(item);
        touch();
    }

    public void touch() {
        this.lastUpdatedAt = Instant.now();
    }

    @Override
    public String toString() {
        return "Cart{" +
                "cartId='" + cartId + '\'' +
                ", items=" + items +
                ", lastUpdatedAt=" + lastUpdatedAt +
                ", checkedOut=" + checkedOut +
                ", abandonedEventSent=" + abandonedEventSent +
                '}';
    }
}
