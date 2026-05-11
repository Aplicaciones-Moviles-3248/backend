package com.upc.courtly.trainingsessions.interfaces.rest.transform;

import com.upc.courtly.trainingsessions.domain.model.aggregates.TrainingSession;
import com.upc.courtly.trainingsessions.interfaces.rest.resources.TrainingSessionResource;

public class TrainingSessionResourceFromEntityAssembler {
    public static TrainingSessionResource toResourceFromEntity(TrainingSession entity) {
        return new TrainingSessionResource(
                entity.getId(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getStatus(),
                entity.getPrice(),
                new TrainingSessionResource.UserSummaryResource(entity.getPlayer().getId(), entity.getPlayer().getName()),
                new TrainingSessionResource.CoachSummaryResource(
                        entity.getCoach().getId(),
                        entity.getCoach().getName(),
                        entity.getCoach().getUser() != null ? entity.getCoach().getUser().getId() : null
                ),
                new TrainingSessionResource.CourtSummaryResource(entity.getCourt().getId(), entity.getCourt().getName()),
                entity.getAvailability().getId()
        );
    }
}
