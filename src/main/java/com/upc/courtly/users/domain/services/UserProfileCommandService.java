package com.upc.courtly.users.domain.services;

import com.upc.courtly.users.domain.model.aggregates.UserProfile;
import com.upc.courtly.users.domain.model.commands.CreateUserProfileCommand;
import com.upc.courtly.users.domain.model.commands.DeleteUserProfileCommand;
import com.upc.courtly.users.domain.model.commands.UpdateUserProfileCommand;
import java.util.Optional;

public interface UserProfileCommandService {
    Optional<UserProfile> handle(CreateUserProfileCommand command);
    Optional<UserProfile> handle(UpdateUserProfileCommand command);
    void handle(DeleteUserProfileCommand command);
}

