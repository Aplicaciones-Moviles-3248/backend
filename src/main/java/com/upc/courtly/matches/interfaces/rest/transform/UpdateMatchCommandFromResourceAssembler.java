package com.upc.courtly.matches.interfaces.rest.transform;

import com.upc.courtly.matches.domain.model.commands.UpdateMatchCommand;
import com.upc.courtly.matches.domain.model.valueobjects.MatchStatus;
import com.upc.courtly.matches.interfaces.rest.resources.UpdateMatchResource;

public class UpdateMatchCommandFromResourceAssembler {
    public static UpdateMatchCommand toCommandFromResource(Long matchId, UpdateMatchResource resource) {
        return new UpdateMatchCommand(
                matchId,
                resource.title(),
                resource.description(),
                resource.dateTime(),
                MatchStatus.valueOf(resource.status()),
                resource.maxPlayers(),
                resource.currentPlayers()
        );
    }
}
