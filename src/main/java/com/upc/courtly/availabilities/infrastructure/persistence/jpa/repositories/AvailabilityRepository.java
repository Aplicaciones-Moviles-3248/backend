package com.upc.courtly.availabilities.infrastructure.persistence.jpa.repositories;

import com.upc.courtly.availabilities.domain.model.aggregates.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
}
