package com.upc.courtly.matches.infrastructure.persistence.jpa.repositories;

import com.upc.courtly.matches.domain.model.aggregates.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    @Override
    @EntityGraph(attributePaths = {"participants", "court", "createdBy"})
    List<Match> findAll();

    @Override
    @EntityGraph(attributePaths = {"participants", "court", "createdBy"})
    Optional<Match> findById(Long id);
}
