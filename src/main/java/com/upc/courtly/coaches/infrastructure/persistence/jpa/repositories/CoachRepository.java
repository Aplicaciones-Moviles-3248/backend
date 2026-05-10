package com.upc.courtly.coaches.infrastructure.persistence.jpa.repositories;

import com.upc.courtly.coaches.domain.model.aggregates.Coach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoachRepository extends JpaRepository<Coach, Long> {
    boolean existsByName(String name);
}

