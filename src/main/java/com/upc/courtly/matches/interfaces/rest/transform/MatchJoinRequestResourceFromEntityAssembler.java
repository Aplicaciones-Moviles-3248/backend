package com.upc.courtly.matches.interfaces.rest.transform;

import com.upc.courtly.matches.domain.model.aggregates.MatchJoinRequest;
import com.upc.courtly.matches.interfaces.rest.resources.MatchJoinRequestResource;

public class MatchJoinRequestResourceFromEntityAssembler {
    public static MatchJoinRequestResource toResourceFromEntity(MatchJoinRequest entity) {
        return new MatchJoinRequestResource(
                entity.getId(),
                entity.getMatch().getId(),
                new MatchJoinRequestResource.UserSummaryResource(entity.getRequester().getId(), entity.getRequester().getName()),
                entity.getStatus().name(),
                entity.getApprovedBy().stream()
                        .map(u -> new MatchJoinRequestResource.UserSummaryResource(u.getId(), u.getName()))
                        .toList(),
                entity.getMatch().getParticipants().size(),
                entity.getCreatedAt(),
                entity.getResolvedAt()
        );
    }
}
