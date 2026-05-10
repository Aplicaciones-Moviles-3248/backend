package com.upc.courtly.analytics.domain.model.commands;

import com.upc.courtly.analytics.domain.model.valueobjects.MetricType;

import java.math.BigDecimal;

public record CreateMetricCommand(MetricType metricType, BigDecimal value, String period, Long coachId) {
}
