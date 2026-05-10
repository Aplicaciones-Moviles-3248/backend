package com.upc.courtly.iam.interfaces.rest.transform;

import com.upc.courtly.iam.domain.model.commands.SignInCommand;
import com.upc.courtly.iam.interfaces.rest.resources.SignInResource;

public class SignInCommandFromResourceAssembler {
    public static SignInCommand toCommandFromResource(SignInResource signInResource) {
        return new SignInCommand(signInResource.username(), signInResource.password());
    }
}
