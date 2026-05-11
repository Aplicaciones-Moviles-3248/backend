package com.upc.courtly.trainingsessions.interfaces.rest.transform;

import com.upc.courtly.trainingsessions.domain.model.commands.CreateTrainingSessionCommand;
import com.upc.courtly.trainingsessions.interfaces.rest.resources.CreateTrainingSessionResource;

public class CreateTrainingSessionCommandFromResourceAssembler {
    public static CreateTrainingSessionCommand toCommandFromResource(CreateTrainingSessionResource resource) {
        return new CreateTrainingSessionCommand(
                resource.playerId(),
                resource.coachId(),
                resource.courtId(),
                resource.availabilityId(),
                resource.startTime(),
                resource.endTime(),
                resource.price()
        );
    }
}
