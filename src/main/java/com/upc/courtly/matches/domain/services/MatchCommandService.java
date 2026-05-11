package com.upc.courtly.matches.domain.services;

import com.upc.courtly.matches.domain.model.aggregates.Match;
import com.upc.courtly.matches.domain.model.commands.CreateMatchCommand;
import com.upc.courtly.matches.domain.model.commands.DeleteMatchCommand;
import com.upc.courtly.matches.domain.model.commands.JoinMatchCommand;
import com.upc.courtly.matches.domain.model.commands.UpdateMatchCommand;

import java.util.Optional;

public interface MatchCommandService {
    Optional<Match> handle(CreateMatchCommand command);
    Optional<Match> handle(UpdateMatchCommand command);
    Optional<Match> handle(JoinMatchCommand command);
    void handle(DeleteMatchCommand command);
}
