package com.upc.courtly.matches.domain.services;

import com.upc.courtly.matches.domain.model.aggregates.MatchJoinRequest;
import com.upc.courtly.matches.domain.model.queries.GetJoinRequestByIdQuery;
import com.upc.courtly.matches.domain.model.queries.GetJoinRequestsByMatchIdQuery;

import java.util.List;
import java.util.Optional;

public interface MatchJoinRequestQueryService {
    List<MatchJoinRequest> handle(GetJoinRequestsByMatchIdQuery query);
    Optional<MatchJoinRequest> handle(GetJoinRequestByIdQuery query);
}
