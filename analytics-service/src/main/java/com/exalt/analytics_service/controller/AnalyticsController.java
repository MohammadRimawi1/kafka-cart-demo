package com.exalt.analytics_service.controller;

import com.exalt.analytics_service.service.AnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Exposes running totals for each event type, so counts can be
 * checked in a browser rather than only via logs.
 *
 * GET /api/analytics/totals
 *
 * @author Mohammad Rimawi
 */
@RestController
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    /**
     * Returns the current running totals for all three event types.
     * LinkedHashMap is used instead of a plain HashMap so the JSON
     * response keeps a consistent, predictable key order
     * (cartCreated, cartCheckedOut, cartAbandoned) rather than
     * whatever order a HashMap happens to iterate in.
     *
     * @return a map of event type name to count, e.g.
     *         { "cartCreated": 5, "cartCheckedOut": 2, "cartAbandoned": 1 }
     */
    @GetMapping("/api/analytics/totals")
    public Map<String, Long> getTotals() {
        Map<String, Long> totals = new LinkedHashMap<>();
        totals.put("cartCreated", analyticsService.getCartCreatedCount());
        totals.put("cartCheckedOut", analyticsService.getCartCheckedOutCount());
        totals.put("cartAbandoned", analyticsService.getCartAbandonedCount());
        return totals;
    }
}