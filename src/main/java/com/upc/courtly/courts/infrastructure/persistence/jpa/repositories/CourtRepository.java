package com.upc.courtly.courts.infrastructure.persistence.jpa.repositories;

import com.upc.courtly.courts.domain.model.aggregates.Court;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourtRepository extends JpaRepository<Court, Long> {
    boolean existsByName(String name);
}

