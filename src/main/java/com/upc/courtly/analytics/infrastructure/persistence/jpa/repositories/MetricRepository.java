package com.upc.courtly.analytics.infrastructure.persistence.jpa.repositories;

import com.upc.courtly.analytics.domain.model.aggregates.Metric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetricRepository extends JpaRepository<Metric, Long> {
}
