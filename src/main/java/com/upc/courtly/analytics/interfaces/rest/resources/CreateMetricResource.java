package com.upc.courtly.analytics.interfaces.rest.resources;

import java.math.BigDecimal;

public record CreateMetricResource(String metricType, BigDecimal value, String period, Long coachId) {
}
