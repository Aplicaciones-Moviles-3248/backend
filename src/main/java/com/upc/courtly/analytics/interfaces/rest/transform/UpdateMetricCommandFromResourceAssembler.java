package com.upc.courtly.analytics.interfaces.rest.transform;

import com.upc.courtly.analytics.domain.model.commands.UpdateMetricCommand;
import com.upc.courtly.analytics.domain.model.valueobjects.MetricType;
import com.upc.courtly.analytics.interfaces.rest.resources.UpdateMetricResource;

public class UpdateMetricCommandFromResourceAssembler {
    public static UpdateMetricCommand toCommandFromResource(Long metricId, UpdateMetricResource resource) {
        return new UpdateMetricCommand(
                metricId,
                MetricType.valueOf(resource.metricType()),
                resource.value(),
                resource.period()
        );
    }
}
