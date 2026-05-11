package com.upc.courtly.trainingsessions.domain.services;

import com.upc.courtly.trainingsessions.domain.model.aggregates.TrainingSession;
import com.upc.courtly.trainingsessions.domain.model.queries.GetAllTrainingSessionsQuery;
import com.upc.courtly.trainingsessions.domain.model.queries.GetTrainingSessionByIdQuery;

import java.util.List;
import java.util.Optional;

public interface TrainingSessionQueryService {
    List<TrainingSession> handle(GetAllTrainingSessionsQuery query);
    Optional<TrainingSession> handle(GetTrainingSessionByIdQuery query);
}
