package com.upc.courtly.matches.interfaces.rest.transform;

import com.upc.courtly.matches.domain.model.aggregates.Match;
import com.upc.courtly.matches.interfaces.rest.resources.MatchResource;

public class MatchResourceFromEntityAssembler {
    public static MatchResource toResourceFromEntity(Match entity) {
        return new MatchResource(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getDateTime(),
                entity.getStatus().name(),
                entity.getMaxPlayers(),
                entity.getCurrentPlayers(),
                entity.getCreatedAt(),
                new MatchResource.CourtSummaryResource(entity.getCourt().getId(), entity.getCourt().getName()),
                new MatchResource.UserSummaryResource(entity.getCreatedBy().getId(), entity.getCreatedBy().getName()),
                entity.getParticipants().stream()
                        .map(participant -> new MatchResource.UserSummaryResource(participant.getId(), participant.getName()))
                        .toList()
        );
    }
}
