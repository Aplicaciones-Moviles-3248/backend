package com.upc.courtly.coaches.interfaces.rest.transform;

import com.upc.courtly.coaches.domain.model.commands.CreateCoachCommand;
import com.upc.courtly.coaches.interfaces.rest.resources.CreateCoachResource;

public class CreateCoachCommandFromResourceAssembler {
    public static CreateCoachCommand toCommandFromResource(CreateCoachResource resource) {
        return new CreateCoachCommand(
                resource.name(),
                resource.expertise(),
                resource.phone()
        );
    }
}

