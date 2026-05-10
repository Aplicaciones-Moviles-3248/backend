package com.upc.courtly.notifications.domain.model.commands;

public record UpdateNotificationCommand(Long notificationId, String title, String message, String type, boolean isRead) {
}
