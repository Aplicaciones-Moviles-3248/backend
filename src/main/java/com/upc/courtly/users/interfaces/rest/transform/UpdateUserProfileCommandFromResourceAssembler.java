package com.upc.courtly.users.interfaces.rest.transform;

import com.upc.courtly.users.domain.model.commands.UpdateUserProfileCommand;
import com.upc.courtly.users.interfaces.rest.resources.UpdateUserProfileResource;

public class UpdateUserProfileCommandFromResourceAssembler {
    public static UpdateUserProfileCommand toCommandFromResource(Long userId, UpdateUserProfileResource resource) {
        return new UpdateUserProfileCommand(
                userId,
                resource.name(),
                resource.email(),
                resource.phone(),
                resource.imageUrl()
        );
    }
}

