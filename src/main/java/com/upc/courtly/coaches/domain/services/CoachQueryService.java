package com.upc.courtly.coaches.domain.services;

import com.upc.courtly.coaches.domain.model.aggregates.Coach;
import com.upc.courtly.coaches.domain.model.queries.GetAllCoachesQuery;
import com.upc.courtly.coaches.domain.model.queries.GetCoachByIdQuery;
import java.util.List;
import java.util.Optional;

public interface CoachQueryService {
    List<Coach> handle(GetAllCoachesQuery query);
    Optional<Coach> handle(GetCoachByIdQuery query);
}

