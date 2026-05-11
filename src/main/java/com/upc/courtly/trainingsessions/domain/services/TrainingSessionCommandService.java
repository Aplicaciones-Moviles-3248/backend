package com.upc.courtly.trainingsessions.domain.services;

import com.upc.courtly.trainingsessions.domain.model.aggregates.TrainingSession;
import com.upc.courtly.trainingsessions.domain.model.commands.*;

import java.util.Optional;

public interface TrainingSessionCommandService {
    Optional<TrainingSession> handle(CreateTrainingSessionCommand command);
    Optional<TrainingSession> handle(AcceptTrainingSessionCommand command);
    Optional<TrainingSession> handle(RejectTrainingSessionCommand command);
    Optional<TrainingSession> handle(CancelTrainingSessionCommand command);
    Optional<TrainingSession> handle(CompleteTrainingSessionCommand command);
    void handle(DeleteTrainingSessionCommand command);
}
