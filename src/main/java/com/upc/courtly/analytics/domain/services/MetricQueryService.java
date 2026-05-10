package com.upc.courtly.analytics.domain.services;

import com.upc.courtly.analytics.domain.model.aggregates.Metric;
import com.upc.courtly.analytics.domain.model.queries.GetAllMetricsQuery;
import com.upc.courtly.analytics.domain.model.queries.GetMetricByIdQuery;

import java.util.List;
import java.util.Optional;

public interface MetricQueryService {
    List<Metric> handle(GetAllMetricsQuery query);
    Optional<Metric> handle(GetMetricByIdQuery query);
}
