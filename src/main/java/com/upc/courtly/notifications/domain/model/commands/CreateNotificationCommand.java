package com.upc.courtly.notifications.domain.model.commands;

public record CreateNotificationCommand(String title, String message, String type, boolean isRead, Long userId) {
}
