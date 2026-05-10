package com.upc.courtly.availabilities.interfaces.rest.transform;

import com.upc.courtly.availabilities.domain.model.aggregates.Availability;
import com.upc.courtly.availabilities.interfaces.rest.resources.AvailabilityResource;

public class AvailabilityResourceFromEntityAssembler {
    public static AvailabilityResource toResourceFromEntity(Availability entity) {
        return new AvailabilityResource(
                entity.getId(),
                entity.getDate(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getStatus().name(),
                entity.getCreatedAt(),
                new AvailabilityResource.CoachSummaryResource(entity.getCoach().getId(), entity.getCoach().getName())
        );
    }
}
