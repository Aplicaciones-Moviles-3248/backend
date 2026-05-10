package com.upc.courtly.iam.interfaces.rest.transform;

import com.upc.courtly.iam.domain.model.aggregates.User;
import com.upc.courtly.iam.interfaces.rest.resources.AuthenticatedUserResource;

public class AuthenticatedUserResourceFromEntityAssembler {
    public static AuthenticatedUserResource toResourceFromEntity(User user, String token) {
        return new AuthenticatedUserResource(user.getId(), user.getUsername(), token);
    }
}
