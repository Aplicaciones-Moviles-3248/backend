package com.upc.courtly.matches.application.internal.queryservices;

import com.upc.courtly.matches.domain.model.aggregates.MatchJoinRequest;
import com.upc.courtly.matches.domain.model.queries.GetJoinRequestByIdQuery;
import com.upc.courtly.matches.domain.model.queries.GetJoinRequestsByMatchIdQuery;
import com.upc.courtly.matches.domain.services.MatchJoinRequestQueryService;
import com.upc.courtly.matches.infrastructure.persistence.jpa.repositories.MatchJoinRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MatchJoinRequestQueryServiceImpl implements MatchJoinRequestQueryService {
    private final MatchJoinRequestRepository matchJoinRequestRepository;

    public MatchJoinRequestQueryServiceImpl(MatchJoinRequestRepository matchJoinRequestRepository) {
        this.matchJoinRequestRepository = matchJoinRequestRepository;
    }

    @Override
    public List<MatchJoinRequest> handle(GetJoinRequestsByMatchIdQuery query) {
        return matchJoinRequestRepository.findByMatchId(query.matchId());
    }

    @Override
    public Optional<MatchJoinRequest> handle(GetJoinRequestByIdQuery query) {
        return matchJoinRequestRepository.findById(query.joinRequestId());
    }
}
