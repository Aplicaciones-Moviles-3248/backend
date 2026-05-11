package com.upc.courtly.analytics.domain.model.aggregates;

import com.upc.courtly.analytics.domain.model.valueobjects.MetricType;
import com.upc.courtly.coaches.domain.model.aggregates.Coach;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "metrics")
@Getter
@Setter
@NoArgsConstructor
public class Metric {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetricType metricType;

    @Column(name = "metric_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal value;

    @Column(nullable = false)
    private String period;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id", nullable = false)
    private Coach coach;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Metric(MetricType metricType, BigDecimal value, String period, Coach coach) {
        this.metricType = metricType;
        this.value = value;
        this.period = period;
        this.coach = coach;
    }

    public void updateMetric(MetricType metricType, BigDecimal value, String period) {
        this.metricType = metricType;
        this.value = value;
        this.period = period;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
