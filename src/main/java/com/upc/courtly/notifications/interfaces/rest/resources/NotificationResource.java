package com.upc.courtly.notifications.interfaces.rest.resources;

import com.upc.courtly.notifications.domain.model.valueobjects.NotificationType;

import java.time.LocalDateTime;

public record NotificationResource(Long id, String title, String message, NotificationType type, boolean isRead,
                                   String relatedEntityType, Long relatedEntityId,
                                   LocalDateTime createdAt, UserSummaryResource user) {
    public record UserSummaryResource(Long id, String name) {}
}
