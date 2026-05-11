package com.upc.courtly.notifications.interfaces.rest.resources;

public record CreateNotificationResource(String title, String message, String type, boolean isRead,
                                         String relatedEntityType, Long relatedEntityId, Long userId) {
}
