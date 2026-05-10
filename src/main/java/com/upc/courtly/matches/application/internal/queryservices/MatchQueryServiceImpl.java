package com.upc.courtly.matches.application.internal.queryservices;

import com.upc.courtly.matches.domain.model.aggregates.Match;
import com.upc.courtly.matches.domain.model.queries.GetAllMatchesQuery;
import com.upc.courtly.matches.domain.model.queries.GetMatchByIdQuery;
import com.upc.courtly.matches.domain.services.MatchQueryService;
import com.upc.courtly.matches.infrastructure.persistence.jpa.repositories.MatchRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MatchQueryServiceImpl implements MatchQueryService {
    private final MatchRepository matchRepository;

    public MatchQueryServiceImpl(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Override
    public List<Match> handle(GetAllMatchesQuery query) {
        return matchRepository.findAll();
    }

    @Override
    public Optional<Match> handle(GetMatchByIdQuery query) {
        return matchRepository.findById(query.matchId());
    }
}
