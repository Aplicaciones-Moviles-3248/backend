package com.upc.courtly.notifications.domain.model.commands;

import com.upc.courtly.notifications.domain.model.valueobjects.NotificationType;

public record UpdateNotificationCommand(Long notificationId, String title, String message, NotificationType type,
                                        boolean isRead, String relatedEntityType, Long relatedEntityId) {
}
