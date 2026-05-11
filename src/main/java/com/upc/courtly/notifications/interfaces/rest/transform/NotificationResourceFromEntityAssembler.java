package com.upc.courtly.notifications.interfaces.rest.transform;

import com.upc.courtly.notifications.domain.model.aggregates.Notification;
import com.upc.courtly.notifications.interfaces.rest.resources.NotificationResource;

public class NotificationResourceFromEntityAssembler {
    public static NotificationResource toResourceFromEntity(Notification entity) {
        return new NotificationResource(
                entity.getId(),
                entity.getTitle(),
                entity.getMessage(),
                entity.getType(),
                entity.isRead(),
                entity.getRelatedEntityType(),
                entity.getRelatedEntityId(),
                entity.getCreatedAt(),
                new NotificationResource.UserSummaryResource(entity.getUser().getId(), entity.getUser().getName())
        );
    }
}
