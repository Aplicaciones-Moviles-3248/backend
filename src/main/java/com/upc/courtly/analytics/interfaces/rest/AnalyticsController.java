package com.upc.courtly.analytics.interfaces.rest;

import com.upc.courtly.iam.interfaces.acl.AuthenticatedContextFacade;
import com.upc.courtly.analytics.domain.services.MetricQueryService;
import com.upc.courtly.analytics.interfaces.rest.resources.MetricResource;
import com.upc.courtly.analytics.interfaces.rest.transform.MetricResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/analytics", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Analytics", description = "Analytics Management Endpoints")
public class AnalyticsController {
    private final MetricQueryService metricQueryService;
    private final AuthenticatedContextFacade authenticatedContextFacade;

    public AnalyticsController(MetricQueryService metricQueryService,
                               AuthenticatedContextFacade authenticatedContextFacade) {
        this.metricQueryService = metricQueryService;
        this.authenticatedContextFacade = authenticatedContextFacade;
    }

    @GetMapping
    public ResponseEntity<List<MetricResource>> getAllMetrics() {
        var currentCoach = authenticatedContextFacade.getAuthenticatedCoachProfile();
        var metrics = metricQueryService.handleCoachMetrics(currentCoach.getId());
        var metricResources = metrics.stream()
                .map(MetricResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(metricResources);
    }

    @GetMapping("/me")
    public ResponseEntity<List<MetricResource>> getMyMetrics() {
        return getAllMetrics();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetricResource> getMetricById(@PathVariable Long id) {
        var currentCoach = authenticatedContextFacade.getAuthenticatedCoachProfile();
        var metric = metricQueryService.handleCoachMetric(currentCoach.getId(), id);
        return metric.map(m -> ResponseEntity.ok(MetricResourceFromEntityAssembler.toResourceFromEntity(m)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createMetric() {
        return rejectManualMutation();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMetric(@PathVariable Long id) {
        return rejectManualMutation();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMetric(@PathVariable Long id) {
        return rejectManualMutation();
    }

    private ResponseEntity<?> rejectManualMutation() {
        throw new ResponseStatusException(org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED,
                "Metrics are derived from completed business operations and cannot be modified manually");
    }
}
