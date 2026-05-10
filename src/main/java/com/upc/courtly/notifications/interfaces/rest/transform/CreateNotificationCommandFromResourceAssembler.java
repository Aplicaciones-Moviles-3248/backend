package com.upc.courtly.notifications.interfaces.rest.transform;

import com.upc.courtly.notifications.domain.model.commands.CreateNotificationCommand;
import com.upc.courtly.notifications.interfaces.rest.resources.CreateNotificationResource;

public class CreateNotificationCommandFromResourceAssembler {
    public static CreateNotificationCommand toCommandFromResource(CreateNotificationResource resource) {
        return new CreateNotificationCommand(
                resource.title(),
                resource.message(),
                resource.type(),
                resource.isRead(),
                resource.userId()
        );
    }
}
