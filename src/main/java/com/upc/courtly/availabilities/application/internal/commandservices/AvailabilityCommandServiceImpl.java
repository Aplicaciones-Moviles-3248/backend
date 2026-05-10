package com.upc.courtly.availabilities.application.internal.commandservices;

import com.upc.courtly.availabilities.domain.model.aggregates.Availability;
import com.upc.courtly.availabilities.domain.model.commands.CreateAvailabilityCommand;
import com.upc.courtly.availabilities.domain.model.commands.DeleteAvailabilityCommand;
import com.upc.courtly.availabilities.domain.model.commands.UpdateAvailabilityCommand;
import com.upc.courtly.availabilities.domain.services.AvailabilityCommandService;
import com.upc.courtly.availabilities.infrastructure.persistence.jpa.repositories.AvailabilityRepository;
import com.upc.courtly.coaches.infrastructure.persistence.jpa.repositories.CoachRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AvailabilityCommandServiceImpl implements AvailabilityCommandService {
    private final AvailabilityRepository availabilityRepository;
    private final CoachRepository coachRepository;

    public AvailabilityCommandServiceImpl(AvailabilityRepository availabilityRepository, CoachRepository coachRepository) {
        this.availabilityRepository = availabilityRepository;
        this.coachRepository = coachRepository;
    }

    @Override
    public Optional<Availability> handle(CreateAvailabilityCommand command) {
        var coach = coachRepository.findById(command.coachId()).orElseThrow(() -> new IllegalArgumentException("Coach with id " + command.coachId() + " not found"));
        var availability = new Availability(command.date(), command.startTime(), command.endTime(), command.status(), coach);
        var createdAvailability = availabilityRepository.save(availability);
        return Optional.of(createdAvailability);
    }

    @Override
    public Optional<Availability> handle(UpdateAvailabilityCommand command) {
        return availabilityRepository.findById(command.availabilityId()).map(availabilityToUpdate -> {
            availabilityToUpdate.updateAvailability(command.date(), command.startTime(), command.endTime(), command.status());
            return availabilityRepository.save(availabilityToUpdate);
        });
    }

    @Override
    public void handle(DeleteAvailabilityCommand command) {
        if (!availabilityRepository.existsById(command.availabilityId())) {
            throw new IllegalArgumentException("Availability with id " + command.availabilityId() + " not found");
        }
        availabilityRepository.deleteById(command.availabilityId());
    }
}
