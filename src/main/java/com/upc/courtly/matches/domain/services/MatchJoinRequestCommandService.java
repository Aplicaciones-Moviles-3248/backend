package com.upc.courtly.matches.domain.services;

import com.upc.courtly.matches.domain.model.aggregates.MatchJoinRequest;
import com.upc.courtly.matches.domain.model.commands.ApproveMatchJoinRequestCommand;
import com.upc.courtly.matches.domain.model.commands.CreateMatchJoinRequestCommand;

import java.util.Optional;

public interface MatchJoinRequestCommandService {
    Optional<MatchJoinRequest> handle(CreateMatchJoinRequestCommand command);
    Optional<MatchJoinRequest> handle(ApproveMatchJoinRequestCommand command);
}
