package com.upc.courtly.users.interfaces.rest.transform;

import com.upc.courtly.users.domain.model.aggregates.UserProfile;
import com.upc.courtly.users.interfaces.rest.resources.UserProfileResource;

public class UserProfileResourceFromEntityAssembler {
    public static UserProfileResource toResourceFromEntity(UserProfile entity) {
        return new UserProfileResource(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPhone()
        );
    }
}

