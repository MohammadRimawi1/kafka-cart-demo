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

    public void incrementCartCreated() {
        cartCreatedCount.incrementAndGet();
    }

    public void incrementCartCheckedOut() {
        cartCheckedOutCount.incrementAndGet();
    }

    public void incrementCartAbandoned() {
        cartAbandonedCount.incrementAndGet();
    }

    public long getCartCreatedCount() {
        return cartCreatedCount.get();
    }

    public long getCartCheckedOutCount() {
        return cartCheckedOutCount.get();
    }

    public long getCartAbandonedCount() {
        return cartAbandonedCount.get();
    }
}