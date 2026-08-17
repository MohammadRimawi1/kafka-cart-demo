package com.exalt.cart_service.service;

import com.exalt.cart_service.config.KafkaTopics;
import com.exalt.cart_service.kafka.CartAbandonedEvent;
import com.exalt.cart_service.kafka.CartCheckedOutEvent;
import com.exalt.cart_service.kafka.CartCreatedEvent;
import com.exalt.cart_service.model.Cart;
import com.exalt.cart_service.model.CartItem;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Orchestrates cart operations.
 *
 * This class is responsible for:
 *  - storing carts in memory (keyed by cartId)
 *  - looking carts up for read/write operations
 *  - delegating state changes to the Cart entity itself
 *    (Cart enforces its own invariants -- this class does not
 *    manipulate Cart's internal state directly)
 *  - publishing the resulting domain events to Kafka after
 *    each successful state change
 *
 * This class deliberately contains NO business rules about what
 * a valid cart mutation looks like. Those rules live in Cart.
 * CartService only coordinates storage + Kafka.
 *
 * @author Mohammad Rimawi
 */
@Service
public class CartService {

    /**
     * In-memory cart storage, keyed by cartId.
     *
     * ConcurrentHashMap is required (not a plain HashMap) because
     * this map will be read concurrently by the abandonment
     * scheduler (a background thread on a timer) while HTTP
     * request threads are writing to it at the same time.
     */
    private final Map<UUID, Cart> cartStore = new ConcurrentHashMap<>();

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * @param kafkaTemplate Spring-managed Kafka producer, auto-configured
     *                       from application.yaml's spring.kafka.* properties
     */
    public CartService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Adds an item to a cart.
     * If cartId is null, a brand-new cart is created first.
     * Publishes a CartCreatedEvent to Kafka after the item is added.
     *
     * @param cartId existing cart id, or null to create a new cart
     * @param item   the item being added
     * @return the cart after the item was added
     * @throws NoSuchElementException if cartId is provided but no such cart exists
     */
    public Cart addItemToCart(UUID cartId, CartItem item) {
        Cart cart = (cartId == null)
                ? createNewCart()
                : getCartOrThrow(cartId);

        cart.addItem(item);

        kafkaTemplate.send(KafkaTopics.CART_CREATED, cart.getCartId().toString(),
                new CartCreatedEvent(
                        cart.getCartId(),
                        item.getProductId(),
                        item.getQuantity(),
                        cart.getLastUpdatedAt()
                ));

        return cart;
    }

    /**
     * Marks a cart as abandoned and publishes a CartAbandonedEvent.
     * Called only by CartAbandonmentScheduler once it decides a cart
     * has gone stale.
     *
     * @param cart the stale cart
     */
    public void markAbandoned(Cart cart) {
        cart.setAbandonedEventSent(true);

        kafkaTemplate.send(KafkaTopics.CART_ABANDONED, cart.getCartId().toString(),
                new CartAbandonedEvent(
                        cart.getCartId(),
                        cart.getLastUpdatedAt(),
                        java.time.Instant.now()
                ));
    }

    /**
     * Checks out a cart.
     * Publishes a CartCheckedOutEvent to Kafka after checkout.
     *
     * @param cartId the cart to check out
     * @return the checked-out cart
     * @throws NoSuchElementException if no cart exists with the given id
     */
    public Cart checkoutCart(UUID cartId) {
        Cart cart = getCartOrThrow(cartId);
        cart.checkout();

        kafkaTemplate.send(KafkaTopics.CART_CHECKED_OUT, cart.getCartId().toString(),
                new CartCheckedOutEvent(
                        cart.getCartId(),
                        cart.getItems().size(),
                        cart.getLastUpdatedAt()
                ));

        return cart;
    }

    /**
     * Returns all carts currently held in memory.
     * Used by the abandonment scheduler to scan every cart's
     * lastUpdatedAt timestamp on each tick.
     *
     * @return the live cart store (not a copy)
     */
    public Map<UUID, Cart> getAllCarts() {
        return cartStore;
    }

    /**
     * Creates a brand-new cart, stores it, and returns it.
     */
    private Cart createNewCart() {
        Cart cart = new Cart();
        cartStore.put(cart.getCartId(), cart);
        return cart;
    }

    /**
     * Looks up a cart by id.
     *
     * @throws NoSuchElementException if the cart does not exist
     */
    private Cart getCartOrThrow(UUID cartId) {
        Cart cart = cartStore.get(cartId);
        if (cart == null) {
            throw new NoSuchElementException("Cart not found: " + cartId);
        }
        return cart;
    }
}