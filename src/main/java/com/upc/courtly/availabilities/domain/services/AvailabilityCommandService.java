package com.upc.courtly.availabilities.domain.services;

import com.upc.courtly.availabilities.domain.model.aggregates.Availability;
import com.upc.courtly.availabilities.domain.model.commands.CreateAvailabilityCommand;
import com.upc.courtly.availabilities.domain.model.commands.DeleteAvailabilityCommand;
import com.upc.courtly.availabilities.domain.model.commands.UpdateAvailabilityCommand;

import java.util.Optional;

public interface AvailabilityCommandService {
    Optional<Availability> handle(CreateAvailabilityCommand command);
    Optional<Availability> handle(UpdateAvailabilityCommand command);
    void handle(DeleteAvailabilityCommand command);
}
