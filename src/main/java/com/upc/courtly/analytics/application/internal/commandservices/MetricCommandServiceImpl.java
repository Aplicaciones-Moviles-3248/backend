package com.upc.courtly.analytics.application.internal.commandservices;

import com.upc.courtly.analytics.domain.model.aggregates.Metric;
import com.upc.courtly.analytics.domain.model.commands.CreateMetricCommand;
import com.upc.courtly.analytics.domain.model.commands.DeleteMetricCommand;
import com.upc.courtly.analytics.domain.model.commands.UpdateMetricCommand;
import com.upc.courtly.analytics.domain.services.MetricCommandService;
import com.upc.courtly.analytics.infrastructure.persistence.jpa.repositories.MetricRepository;
import com.upc.courtly.coaches.infrastructure.persistence.jpa.repositories.CoachRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MetricCommandServiceImpl implements MetricCommandService {
    private final MetricRepository metricRepository;
    private final CoachRepository coachRepository;

    public MetricCommandServiceImpl(MetricRepository metricRepository, CoachRepository coachRepository) {
        this.metricRepository = metricRepository;
        this.coachRepository = coachRepository;
    }

    @Override
    public Optional<Metric> handle(CreateMetricCommand command) {
        var coach = coachRepository.findById(command.coachId()).orElseThrow(() -> new IllegalArgumentException("Coach with id " + command.coachId() + " not found"));
        var metric = new Metric(command.metricType(), command.value(), command.period(), coach);
        var createdMetric = metricRepository.save(metric);
        return Optional.of(createdMetric);
    }

    @Override
    public Optional<Metric> handle(UpdateMetricCommand command) {
        return metricRepository.findById(command.metricId()).map(metricToUpdate -> {
            metricToUpdate.updateMetric(command.metricType(), command.value(), command.period());
            return metricRepository.save(metricToUpdate);
        });
    }

    @Override
    public void handle(DeleteMetricCommand command) {
        if (!metricRepository.existsById(command.metricId())) {
            throw new IllegalArgumentException("Metric with id " + command.metricId() + " not found");
        }
        metricRepository.deleteById(command.metricId());
    }
}
