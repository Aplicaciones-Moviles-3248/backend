package com.upc.courtly.coaches.interfaces.rest.transform;

import com.upc.courtly.coaches.domain.model.commands.UpdateCoachCommand;
import com.upc.courtly.coaches.interfaces.rest.resources.UpdateCoachResource;

public class UpdateCoachCommandFromResourceAssembler {
    public static UpdateCoachCommand toCommandFromResource(Long coachId, UpdateCoachResource resource) {
        return new UpdateCoachCommand(
                coachId,
                resource.name(),
                resource.expertise(),
                resource.phone()
        );
    }
}

