package com.upc.courtly.coaches.infrastructure.persistence.jpa.repositories;

import com.upc.courtly.coaches.domain.model.aggregates.Coach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoachRepository extends JpaRepository<Coach, Long> {
    boolean existsByName(String name);
    Optional<Coach> findByName(String name);
    Optional<Coach> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}

