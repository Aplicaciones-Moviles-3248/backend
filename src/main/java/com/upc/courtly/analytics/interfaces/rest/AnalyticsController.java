package com.upc.courtly.analytics.interfaces.rest;

import com.upc.courtly.analytics.domain.model.commands.DeleteMetricCommand;
import com.upc.courtly.analytics.domain.model.queries.GetAllMetricsQuery;
import com.upc.courtly.analytics.domain.model.queries.GetMetricByIdQuery;
import com.upc.courtly.analytics.domain.services.MetricCommandService;
import com.upc.courtly.analytics.domain.services.MetricQueryService;
import com.upc.courtly.analytics.interfaces.rest.resources.CreateMetricResource;
import com.upc.courtly.analytics.interfaces.rest.resources.MetricResource;
import com.upc.courtly.analytics.interfaces.rest.resources.UpdateMetricResource;
import com.upc.courtly.analytics.interfaces.rest.transform.CreateMetricCommandFromResourceAssembler;
import com.upc.courtly.analytics.interfaces.rest.transform.MetricResourceFromEntityAssembler;
import com.upc.courtly.analytics.interfaces.rest.transform.UpdateMetricCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/analytics", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Analytics", description = "Analytics Management Endpoints")
public class AnalyticsController {
    private final MetricCommandService metricCommandService;
    private final MetricQueryService metricQueryService;

    public AnalyticsController(MetricCommandService metricCommandService, MetricQueryService metricQueryService) {
        this.metricCommandService = metricCommandService;
        this.metricQueryService = metricQueryService;
    }

    @PostMapping
    public ResponseEntity<MetricResource> createMetric(@RequestBody CreateMetricResource resource) {
        var command = CreateMetricCommandFromResourceAssembler.toCommandFromResource(resource);
        var metric = metricCommandService.handle(command);
        return metric.map(m -> new ResponseEntity<>(MetricResourceFromEntityAssembler.toResourceFromEntity(m), HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping
    public ResponseEntity<List<MetricResource>> getAllMetrics() {
        var query = new GetAllMetricsQuery();
        var metrics = metricQueryService.handle(query);
        var metricResources = metrics.stream()
                .map(MetricResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(metricResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetricResource> getMetricById(@PathVariable Long id) {
        var query = new GetMetricByIdQuery(id);
        var metric = metricQueryService.handle(query);
        return metric.map(m -> ResponseEntity.ok(MetricResourceFromEntityAssembler.toResourceFromEntity(m)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetricResource> updateMetric(@PathVariable Long id, @RequestBody UpdateMetricResource resource) {
        var command = UpdateMetricCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var updatedMetric = metricCommandService.handle(command);
        return updatedMetric.map(m -> ResponseEntity.ok(MetricResourceFromEntityAssembler.toResourceFromEntity(m)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMetric(@PathVariable Long id) {
        var command = new DeleteMetricCommand(id);
        metricCommandService.handle(command);
        return ResponseEntity.ok("Metric deleted successfully.");
    }
}
