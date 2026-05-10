package com.upc.courtly.courts.interfaces.rest.transform;

import com.upc.courtly.courts.domain.model.commands.CreateCourtCommand;
import com.upc.courtly.courts.interfaces.rest.resources.CreateCourtResource;

public class CreateCourtCommandFromResourceAssembler {
    public static CreateCourtCommand toCommandFromResource(CreateCourtResource resource) {
        return new CreateCourtCommand(
                resource.name(),
                resource.location(),
                resource.type()
        );
    }
}

