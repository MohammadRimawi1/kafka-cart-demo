package com.exalt.analytics_service.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Holds running totals for each event type analytics-service has seen.
 * AtomicLong is used because these counters are incremented from Kafka
 * listener threads and read from HTTP request threads concurrently.
 *
 * @author Mohammad Rimawi
 */
@Service
public class AnalyticsService {

    private final AtomicLong cartCreatedCount = new AtomicLong(0);
    private final AtomicLong cartCheckedOutCount = new AtomicLong(0);
    private final AtomicLong cartAbandonedCount = new AtomicLong(0);

    /**
     * Called by CartEventListener whenever a cart-created event is
     * received. Increments the running total by one, atomically.
     */
    public void incrementCartCreated() {
        cartCreatedCount.incrementAndGet();
    }

    /**
     * Called by CartEventListener whenever a cart-checked-out event
     * is received. Increments the running total by one, atomically.
     */
    public void incrementCartCheckedOut() {
        cartCheckedOutCount.incrementAndGet();
    }

    /**
     * Called by CartEventListener whenever a cart-abandoned event is
     * received. Increments the running total by one, atomically.
     */
    public void incrementCartAbandoned() {
        cartAbandonedCount.incrementAndGet();
    }

    /**
     * @return total number of cart-created events seen since this
     *         service started (counters are in-memory, not persisted)
     */
    public long getCartCreatedCount() {
        return cartCreatedCount.get();
    }

    /**
     * @return total number of cart-checked-out events seen since this
     *         service started
     */
    public long getCartCheckedOutCount() {
        return cartCheckedOutCount.get();
    }

    /**
     * @return total number of cart-abandoned events seen since this
     *         service started
     */
    public long getCartAbandonedCount() {
        return cartAbandonedCount.get();
    }
}