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
        if (command.startTime().compareTo(command.endTime()) >= 0) {
            throw new IllegalArgumentException("Availability start time must be before end time");
        }
        if (availabilityRepository.existsOverlappingAvailability(command.coachId(), command.date(), command.startTime(), command.endTime(), null)) {
            throw new IllegalArgumentException("Coach already has an overlapping availability in that time range");
        }
        var availability = new Availability(command.date(), command.startTime(), command.endTime(), command.status(), coach);
        var createdAvailability = availabilityRepository.save(availability);
        return Optional.of(createdAvailability);
    }

    @Override
    public Optional<Availability> handle(UpdateAvailabilityCommand command) {
        return availabilityRepository.findById(command.availabilityId()).map(availabilityToUpdate -> {
            if (availabilityToUpdate.getStatus() == com.upc.courtly.availabilities.domain.model.valueobjects.AvailabilityStatus.RESERVED) {
                throw new IllegalArgumentException("Reserved availabilities cannot be modified");
            }
            if (command.startTime().compareTo(command.endTime()) >= 0) {
                throw new IllegalArgumentException("Availability start time must be before end time");
            }
            if (availabilityRepository.existsOverlappingAvailability(availabilityToUpdate.getCoach().getId(), command.date(), command.startTime(), command.endTime(), availabilityToUpdate.getId())) {
                throw new IllegalArgumentException("Coach already has an overlapping availability in that time range");
            }
            availabilityToUpdate.updateAvailability(command.date(), command.startTime(), command.endTime(), command.status());
            return availabilityRepository.save(availabilityToUpdate);
        });
    }

    @Override
    public void handle(DeleteAvailabilityCommand command) {
        var availability = availabilityRepository.findById(command.availabilityId())
                .orElseThrow(() -> new IllegalArgumentException("Availability with id " + command.availabilityId() + " not found"));
        if (availability.getStatus() == com.upc.courtly.availabilities.domain.model.valueobjects.AvailabilityStatus.RESERVED) {
            throw new IllegalArgumentException("Reserved availabilities cannot be deleted");
        }
        availabilityRepository.deleteById(command.availabilityId());
    }
}
