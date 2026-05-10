package com.upc.courtly.coaches.application.internal.queryservices;

import com.upc.courtly.coaches.domain.model.aggregates.Coach;
import com.upc.courtly.coaches.domain.model.queries.GetAllCoachesQuery;
import com.upc.courtly.coaches.domain.model.queries.GetCoachByIdQuery;
import com.upc.courtly.coaches.domain.services.CoachQueryService;
import com.upc.courtly.coaches.infrastructure.persistence.jpa.repositories.CoachRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CoachQueryServiceImpl implements CoachQueryService {
    private final CoachRepository coachRepository;

    public CoachQueryServiceImpl(CoachRepository coachRepository) {
        this.coachRepository = coachRepository;
    }

    @Override
    public List<Coach> handle(GetAllCoachesQuery query) {
        return coachRepository.findAll();
    }

    @Override
    public Optional<Coach> handle(GetCoachByIdQuery query) {
        return coachRepository.findById(query.coachId());
    }
}

