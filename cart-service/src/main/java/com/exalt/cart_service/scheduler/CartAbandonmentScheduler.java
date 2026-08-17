package com.exalt.cart_service.scheduler;

import com.exalt.cart_service.model.Cart;
import com.exalt.cart_service.service.CartService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

/**
 * Background job that periodically scans all in-memory carts and
 * flags ones that have gone stale (not checked out, not touched
 * recently) as abandoned, publishing a cart-abandoned event for each.
 *
 * This is the piece of the project that has no external trigger --
 * nothing calls this, nothing requests it. It wakes up on its own
 * timer and notices staleness by comparing "now" against each
 * cart's lastUpdatedAt.
 *
 * @author Mohammad Rimawi
 */
@Component
public class CartAbandonmentScheduler {

    /** How long a cart can sit untouched before it's considered abandoned. */
    private static final Duration ABANDONMENT_THRESHOLD = Duration.ofMinutes(2);

    private final CartService cartService;

    public CartAbandonmentScheduler(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Runs every 60 seconds (fixedRate = time between the START of
     * one run and the START of the next, in milliseconds).
     *
     * On each tick: looks at every cart in memory, skips carts that
     * are already checked out or already flagged abandoned, and
     * publishes a cart-abandoned event for any cart whose
     * lastUpdatedAt is older than ABANDONMENT_THRESHOLD.
     */
    @Scheduled(fixedRate = 60000)
    public void checkForAbandonedCarts() {
        Instant now = Instant.now();

        for (Cart cart : cartService.getAllCarts().values()) {
            if (cart.isCheckedOut() || cart.isAbandonedEventSent()) {
                continue;
            }

            Duration idleTime = Duration.between(cart.getLastUpdatedAt(), now);
            if (idleTime.compareTo(ABANDONMENT_THRESHOLD) >= 0) {
                cartService.markAbandoned(cart);
            }
        }
    }
}