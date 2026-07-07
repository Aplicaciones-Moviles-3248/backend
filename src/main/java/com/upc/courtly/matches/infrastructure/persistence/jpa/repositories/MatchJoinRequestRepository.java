package com.upc.courtly.matches.infrastructure.persistence.jpa.repositories;

import com.upc.courtly.matches.domain.model.aggregates.MatchJoinRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchJoinRequestRepository extends JpaRepository<MatchJoinRequest, Long> {
    @EntityGraph(attributePaths = {"match", "match.participants", "requester", "approvedBy"})
    List<MatchJoinRequest> findByMatchId(Long matchId);

    @Override
    @EntityGraph(attributePaths = {"match", "match.participants", "requester", "approvedBy"})
    Optional<MatchJoinRequest> findById(Long id);
}
