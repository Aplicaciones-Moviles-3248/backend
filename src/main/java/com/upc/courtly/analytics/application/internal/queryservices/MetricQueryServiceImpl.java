package com.upc.courtly.analytics.application.internal.queryservices;

import com.upc.courtly.analytics.domain.model.aggregates.Metric;
import com.upc.courtly.analytics.domain.model.queries.GetAllMetricsQuery;
import com.upc.courtly.analytics.domain.model.queries.GetMetricByIdQuery;
import com.upc.courtly.analytics.domain.services.MetricQueryService;
import com.upc.courtly.analytics.infrastructure.persistence.jpa.repositories.MetricRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MetricQueryServiceImpl implements MetricQueryService {
    private final MetricRepository metricRepository;

    public MetricQueryServiceImpl(MetricRepository metricRepository) {
        this.metricRepository = metricRepository;
    }

    @Override
    public List<Metric> handle(GetAllMetricsQuery query) {
        return metricRepository.findAll();
    }

    @Override
    public Optional<Metric> handle(GetMetricByIdQuery query) {
        return metricRepository.findById(query.metricId());
    }
}
