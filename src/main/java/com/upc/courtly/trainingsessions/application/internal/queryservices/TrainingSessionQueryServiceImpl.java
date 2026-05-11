package com.upc.courtly.trainingsessions.application.internal.queryservices;

import com.upc.courtly.trainingsessions.domain.model.aggregates.TrainingSession;
import com.upc.courtly.trainingsessions.domain.model.queries.GetAllTrainingSessionsQuery;
import com.upc.courtly.trainingsessions.domain.model.queries.GetTrainingSessionByIdQuery;
import com.upc.courtly.trainingsessions.domain.services.TrainingSessionQueryService;
import com.upc.courtly.trainingsessions.infrastructure.persistence.jpa.repositories.TrainingSessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainingSessionQueryServiceImpl implements TrainingSessionQueryService {
    private final TrainingSessionRepository trainingSessionRepository;

    public TrainingSessionQueryServiceImpl(TrainingSessionRepository trainingSessionRepository) {
        this.trainingSessionRepository = trainingSessionRepository;
    }

    @Override
    public List<TrainingSession> handle(GetAllTrainingSessionsQuery query) {
        return trainingSessionRepository.findAll();
    }

    @Override
    public Optional<TrainingSession> handle(GetTrainingSessionByIdQuery query) {
        return trainingSessionRepository.findById(query.trainingSessionId());
    }
}
