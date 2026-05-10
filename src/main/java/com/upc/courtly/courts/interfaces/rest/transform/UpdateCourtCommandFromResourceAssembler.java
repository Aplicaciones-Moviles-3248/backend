package com.upc.courtly.courts.interfaces.rest.transform;

import com.upc.courtly.courts.domain.model.commands.UpdateCourtCommand;
import com.upc.courtly.courts.interfaces.rest.resources.UpdateCourtResource;

public class UpdateCourtCommandFromResourceAssembler {
    public static UpdateCourtCommand toCommandFromResource(Long courtId, UpdateCourtResource resource) {
        return new UpdateCourtCommand(
                courtId,
                resource.name(),
                resource.location(),
                resource.type()
        );
    }
}
