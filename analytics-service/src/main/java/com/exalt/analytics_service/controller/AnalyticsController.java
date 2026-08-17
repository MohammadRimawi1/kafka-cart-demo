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

    @GetMapping("/api/analytics/totals")
    public Map<String, Long> getTotals() {
        Map<String, Long> totals = new LinkedHashMap<>();
        totals.put("cartCreated", analyticsService.getCartCreatedCount());
        totals.put("cartCheckedOut", analyticsService.getCartCheckedOutCount());
        totals.put("cartAbandoned", analyticsService.getCartAbandonedCount());
        return totals;
    }
}