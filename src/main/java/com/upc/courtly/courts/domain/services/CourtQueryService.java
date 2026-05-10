package com.upc.courtly.courts.domain.services;

import com.upc.courtly.courts.domain.model.aggregates.Court;
import com.upc.courtly.courts.domain.model.queries.GetAllCourtsQuery;
import com.upc.courtly.courts.domain.model.queries.GetCourtByIdQuery;
import java.util.List;
import java.util.Optional;

public interface CourtQueryService {
    List<Court> handle(GetAllCourtsQuery query);
    Optional<Court> handle(GetCourtByIdQuery query);
}

