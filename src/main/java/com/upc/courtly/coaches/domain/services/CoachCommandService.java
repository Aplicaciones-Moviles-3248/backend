package com.upc.courtly.coaches.domain.services;

import com.upc.courtly.coaches.domain.model.aggregates.Coach;
import com.upc.courtly.coaches.domain.model.commands.CreateCoachCommand;
import com.upc.courtly.coaches.domain.model.commands.DeleteCoachCommand;
import com.upc.courtly.coaches.domain.model.commands.UpdateCoachCommand;
import java.util.Optional;

public interface CoachCommandService {
    Optional<Coach> handle(CreateCoachCommand command);
    Optional<Coach> handle(UpdateCoachCommand command);
    void handle(DeleteCoachCommand command);
}

