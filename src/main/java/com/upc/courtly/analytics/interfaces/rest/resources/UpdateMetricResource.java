package com.upc.courtly.analytics.interfaces.rest.resources;

import java.math.BigDecimal;

public record UpdateMetricResource(String metricType, BigDecimal value, String period) {
}
