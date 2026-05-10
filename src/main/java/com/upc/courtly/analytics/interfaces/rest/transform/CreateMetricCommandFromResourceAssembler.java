package com.upc.courtly.analytics.interfaces.rest.transform;

import com.upc.courtly.analytics.domain.model.commands.CreateMetricCommand;
import com.upc.courtly.analytics.domain.model.valueobjects.MetricType;
import com.upc.courtly.analytics.interfaces.rest.resources.CreateMetricResource;

public class CreateMetricCommandFromResourceAssembler {
    public static CreateMetricCommand toCommandFromResource(CreateMetricResource resource) {
        return new CreateMetricCommand(
                MetricType.valueOf(resource.metricType()),
                resource.value(),
                resource.period(),
                resource.coachId()
        );
    }
}
