package com.upc.courtly.availabilities.interfaces.rest.transform;

import com.upc.courtly.availabilities.domain.model.commands.UpdateAvailabilityCommand;
import com.upc.courtly.availabilities.domain.model.valueobjects.AvailabilityStatus;
import com.upc.courtly.availabilities.interfaces.rest.resources.UpdateAvailabilityResource;

public class UpdateAvailabilityCommandFromResourceAssembler {
    public static UpdateAvailabilityCommand toCommandFromResource(Long availabilityId, UpdateAvailabilityResource resource) {
        return new UpdateAvailabilityCommand(
                availabilityId,
                resource.date(),
                resource.startTime(),
                resource.endTime(),
                AvailabilityStatus.valueOf(resource.status())
        );
    }
}
