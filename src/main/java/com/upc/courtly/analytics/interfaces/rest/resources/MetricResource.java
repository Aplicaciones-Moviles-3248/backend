package com.upc.courtly.analytics.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MetricResource(Long id, String metricType, BigDecimal value, String period, LocalDateTime createdAt, CoachSummaryResource coach) {
    public record CoachSummaryResource(Long id, String name) {}
}
