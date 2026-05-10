package com.upc.courtly.analytics.domain.model.commands;

import com.upc.courtly.analytics.domain.model.valueobjects.MetricType;

import java.math.BigDecimal;

public record UpdateMetricCommand(Long metricId, MetricType metricType, BigDecimal value, String period) {
}
