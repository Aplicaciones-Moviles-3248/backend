package com.upc.courtly.notifications.interfaces.rest.transform;

import com.upc.courtly.notifications.domain.model.commands.UpdateNotificationCommand;
import com.upc.courtly.notifications.domain.model.valueobjects.NotificationType;
import com.upc.courtly.notifications.interfaces.rest.resources.UpdateNotificationResource;

public class UpdateNotificationCommandFromResourceAssembler {
    public static UpdateNotificationCommand toCommandFromResource(Long notificationId, UpdateNotificationResource resource) {
        return new UpdateNotificationCommand(
                notificationId,
                resource.title(),
                resource.message(),
                NotificationType.valueOf(resource.type()),
                resource.isRead(),
                resource.relatedEntityType(),
                resource.relatedEntityId()
        );
    }
}
