package com.upc.courtly.users.interfaces.rest.transform;

import com.upc.courtly.users.domain.model.commands.CreateUserProfileCommand;
import com.upc.courtly.users.interfaces.rest.resources.CreateUserProfileResource;

public class CreateUserProfileCommandFromResourceAssembler {
    public static CreateUserProfileCommand toCommandFromResource(CreateUserProfileResource resource) {
        return new CreateUserProfileCommand(
                resource.name(),
                resource.email(),
                resource.phone()
        );
    }
}

