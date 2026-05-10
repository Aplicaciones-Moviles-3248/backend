package com.upc.courtly.analytics.domain.services;

import com.upc.courtly.analytics.domain.model.aggregates.Metric;
import com.upc.courtly.analytics.domain.model.commands.CreateMetricCommand;
import com.upc.courtly.analytics.domain.model.commands.DeleteMetricCommand;
import com.upc.courtly.analytics.domain.model.commands.UpdateMetricCommand;

import java.util.Optional;

public interface MetricCommandService {
    Optional<Metric> handle(CreateMetricCommand command);
    Optional<Metric> handle(UpdateMetricCommand command);
    void handle(DeleteMetricCommand command);
}
