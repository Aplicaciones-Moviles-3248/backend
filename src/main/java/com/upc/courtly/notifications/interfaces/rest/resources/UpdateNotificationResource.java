package com.upc.courtly.notifications.interfaces.rest.resources;

public record UpdateNotificationResource(String title, String message, String type, boolean isRead,
                                         String relatedEntityType, Long relatedEntityId) {
}
