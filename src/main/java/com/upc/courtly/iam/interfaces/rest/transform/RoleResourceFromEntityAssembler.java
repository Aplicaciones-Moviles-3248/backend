package com.upc.courtly.iam.interfaces.rest.transform;

import com.upc.courtly.iam.domain.model.entities.Role;
import com.upc.courtly.iam.interfaces.rest.resources.RoleResource;

public class RoleResourceFromEntityAssembler {
    public static RoleResource toResourceFromEntity(Role role) {
        return new RoleResource(role.getId(), role.getStringName());
    }
}
