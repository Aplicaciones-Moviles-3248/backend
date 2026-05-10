package com.upc.courtly.availabilities.interfaces.rest.transform;

import com.upc.courtly.availabilities.domain.model.commands.CreateAvailabilityCommand;
import com.upc.courtly.availabilities.domain.model.valueobjects.AvailabilityStatus;
import com.upc.courtly.availabilities.interfaces.rest.resources.CreateAvailabilityResource;

public class CreateAvailabilityCommandFromResourceAssembler {
    public static CreateAvailabilityCommand toCommandFromResource(CreateAvailabilityResource resource) {
        return new CreateAvailabilityCommand(
                resource.date(),
                resource.startTime(),
                resource.endTime(),
                AvailabilityStatus.valueOf(resource.status()),
                resource.coachId()
        );
    }
}
