package com.upc.courtly.analytics.interfaces.rest.transform;

import com.upc.courtly.analytics.domain.model.aggregates.Metric;
import com.upc.courtly.analytics.interfaces.rest.resources.MetricResource;

public class MetricResourceFromEntityAssembler {
    public static MetricResource toResourceFromEntity(Metric entity) {
        return new MetricResource(
                entity.getId(),
                entity.getMetricType().name(),
                entity.getValue(),
                entity.getPeriod(),
                entity.getCreatedAt(),
                new MetricResource.CoachSummaryResource(entity.getCoach().getId(), entity.getCoach().getName())
        );
    }
}
