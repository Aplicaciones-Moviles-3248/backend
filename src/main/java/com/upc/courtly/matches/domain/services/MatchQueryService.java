package com.upc.courtly.matches.domain.services;

import com.upc.courtly.matches.domain.model.aggregates.Match;
import com.upc.courtly.matches.domain.model.queries.GetAllMatchesQuery;
import com.upc.courtly.matches.domain.model.queries.GetMatchByIdQuery;

import java.util.List;
import java.util.Optional;

public interface MatchQueryService {
    List<Match> handle(GetAllMatchesQuery query);
    Optional<Match> handle(GetMatchByIdQuery query);
}
