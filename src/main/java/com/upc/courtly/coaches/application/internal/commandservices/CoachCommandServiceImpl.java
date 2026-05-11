package com.upc.courtly.coaches.application.internal.commandservices;

import com.upc.courtly.coaches.domain.model.aggregates.Coach;
import com.upc.courtly.coaches.domain.model.commands.CreateCoachCommand;
import com.upc.courtly.coaches.domain.model.commands.DeleteCoachCommand;
import com.upc.courtly.coaches.domain.model.commands.UpdateCoachCommand;
import com.upc.courtly.coaches.domain.services.CoachCommandService;
import com.upc.courtly.coaches.infrastructure.persistence.jpa.repositories.CoachRepository;
import com.upc.courtly.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CoachCommandServiceImpl implements CoachCommandService {
    private final CoachRepository coachRepository;
    private final UserRepository userRepository;

    public CoachCommandServiceImpl(CoachRepository coachRepository, UserRepository userRepository) {
        this.coachRepository = coachRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<Coach> handle(CreateCoachCommand command) {
        if (coachRepository.existsByName(command.name())) {
            throw new IllegalArgumentException("Coach with name " + command.name() + " already exists");
        }
        if (command.userId() != null && coachRepository.existsByUserId(command.userId())) {
            throw new IllegalArgumentException("Coach profile for iam user " + command.userId() + " already exists");
        }
        var user = command.userId() == null ? null :
                userRepository.findById(command.userId()).orElseThrow(() -> new IllegalArgumentException("IAM user with id " + command.userId() + " not found"));
        var coach = new Coach(command.name(), command.expertise(), command.phone(), user);
        var createdCoach = coachRepository.save(coach);
        return Optional.of(createdCoach);
    }

    @Override
    public Optional<Coach> handle(UpdateCoachCommand command) {
        return coachRepository.findById(command.coachId()).map(coachToUpdate -> {
            coachToUpdate.updateCoach(command.name(), command.expertise(), command.phone());
            return coachRepository.save(coachToUpdate);
        });
    }

    @Override
    public void handle(DeleteCoachCommand command) {
        if (!coachRepository.existsById(command.coachId())) {
            throw new IllegalArgumentException("Coach with id " + command.coachId() + " not found");
        }
        coachRepository.deleteById(command.coachId());
    }
}

