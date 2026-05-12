package com.upc.courtly.analytics.domain.model.aggregates;

import com.upc.courtly.analytics.domain.model.valueobjects.MetricType;
import com.upc.courtly.coaches.domain.model.aggregates.Coach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class MetricTest {

    @Test
    @DisplayName("Debería crear una métrica correctamente con los valores enviados")
    void shouldCreateMetricCorrectly() {
        Coach coach = new Coach();

        Metric metric = new Metric(
                MetricType.SESSIONS_COMPLETED,
                new BigDecimal("25.50"),
                "2026-Q1",
                coach
        );

        assertEquals(MetricType.SESSIONS_COMPLETED, metric.getMetricType());
        assertEquals(new BigDecimal("25.50"), metric.getValue());
        assertEquals("2026-Q1", metric.getPeriod());
        assertEquals(coach, metric.getCoach());
    }

    @Test
    @DisplayName("Debería actualizar correctamente los campos de la métrica")
    void shouldUpdateMetricCorrectly() {
        Coach coach = new Coach();

        Metric metric = new Metric(
                MetricType.SESSIONS_COMPLETED,
                new BigDecimal("10.00"),
                "2026-Q1",
                coach
        );

        metric.updateMetric(
                MetricType.REVENUE_TOTAL,
                new BigDecimal("15.75"),
                "2026-Q2"
        );

        assertEquals(MetricType.REVENUE_TOTAL, metric.getMetricType());
        assertEquals(new BigDecimal("15.75"), metric.getValue());
        assertEquals("2026-Q2", metric.getPeriod());
    }

    @Test
    @DisplayName("Debería asignar correctamente el ID de la métrica")
    void shouldAssignIdCorrectly() {
        Metric metric = new Metric();
        metric.setId(100L);

        assertEquals(100L, metric.getId());
    }

    @Test
    @DisplayName("Debería asignar correctamente la fecha de creación")
    void shouldAssignCreatedAtCorrectly() {
        Metric metric = new Metric();
        LocalDateTime now = LocalDateTime.now();

        metric.setCreatedAt(now);

        assertEquals(now, metric.getCreatedAt());
    }

    @Test
    @DisplayName("Debería generar la fecha de creación al persistirse")
    void shouldGenerateCreatedAtOnPersist() {
        Metric metric = new Metric();

        metric.onCreate();

        assertNotNull(metric.getCreatedAt());
    }
}