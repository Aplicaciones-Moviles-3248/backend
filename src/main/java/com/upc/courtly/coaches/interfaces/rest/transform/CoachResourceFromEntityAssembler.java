package com.upc.courtly.coaches.interfaces.rest.transform;

import com.upc.courtly.coaches.domain.model.aggregates.Coach;
import com.upc.courtly.coaches.interfaces.rest.resources.CoachResource;

public class CoachResourceFromEntityAssembler {
    public static CoachResource toResourceFromEntity(Coach entity) {
        return new CoachResource(
                entity.getId(),
                entity.getName(),
                entity.getExpertise(),
                entity.getPhone(),
                entity.getUser() != null ? entity.getUser().getId() : null
        );
    }
}

