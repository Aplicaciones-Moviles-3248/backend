package com.upc.courtly.matches.infrastructure.persistence.jpa.repositories;

import com.upc.courtly.matches.domain.model.aggregates.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
}
