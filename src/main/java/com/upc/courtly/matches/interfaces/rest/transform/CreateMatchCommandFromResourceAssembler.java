package com.upc.courtly.matches.interfaces.rest.transform;

import com.upc.courtly.matches.domain.model.commands.CreateMatchCommand;
import com.upc.courtly.matches.domain.model.valueobjects.MatchStatus;
import com.upc.courtly.matches.interfaces.rest.resources.CreateMatchResource;

public class CreateMatchCommandFromResourceAssembler {
    public static CreateMatchCommand toCommandFromResource(CreateMatchResource resource) {
        return new CreateMatchCommand(
                resource.title(),
                resource.description(),
                resource.dateTime(),
                MatchStatus.valueOf(resource.status()),
                resource.maxPlayers(),
                resource.currentPlayers(),
                resource.courtId(),
                resource.createdById()
        );
    }
}
