package com.upc.courtly.users.application.internal.commandservices;

import com.upc.courtly.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.upc.courtly.users.domain.model.aggregates.UserProfile;
import com.upc.courtly.users.domain.model.commands.CreateUserProfileCommand;
import com.upc.courtly.users.domain.model.commands.DeleteUserProfileCommand;
import com.upc.courtly.users.domain.model.commands.UpdateUserProfileCommand;
import com.upc.courtly.users.domain.services.UserProfileCommandService;
import com.upc.courtly.users.infrastructure.persistence.jpa.repositories.UserProfileRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserProfileCommandServiceImpl implements UserProfileCommandService {
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    public UserProfileCommandServiceImpl(UserProfileRepository userProfileRepository, UserRepository userRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserProfile> handle(CreateUserProfileCommand command) {
        if (userProfileRepository.existsByEmail(command.email())) {
            throw new IllegalArgumentException("User with email " + command.email() + " already exists");
        }
        if (command.userId() != null && userProfileRepository.existsByUserId(command.userId())) {
            throw new IllegalArgumentException("User profile for iam user " + command.userId() + " already exists");
        }
        var user = command.userId() == null ? null :
                userRepository.findById(command.userId()).orElseThrow(() -> new IllegalArgumentException("IAM user with id " + command.userId() + " not found"));
        var userProfile = new UserProfile(command.name(), command.email(), command.phone(), command.imageUrl(), user);
        var createdUserProfile = userProfileRepository.save(userProfile);
        return Optional.of(createdUserProfile);
    }

    @Override
    public Optional<UserProfile> handle(UpdateUserProfileCommand command) {
        return userProfileRepository.findById(command.userId()).map(userProfileToUpdate -> {
            userProfileToUpdate.updateProfile(command.name(), command.email(), command.phone(), command.imageUrl());
            return userProfileRepository.save(userProfileToUpdate);
        });
    }

    @Override
    public void handle(DeleteUserProfileCommand command) {
        if (!userProfileRepository.existsById(command.userId())) {
            throw new IllegalArgumentException("User with id " + command.userId() + " not found");
        }
        userProfileRepository.deleteById(command.userId());
    }
}

