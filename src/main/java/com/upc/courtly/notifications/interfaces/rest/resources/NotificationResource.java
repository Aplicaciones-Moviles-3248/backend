package com.upc.courtly.notifications.interfaces.rest.resources;

import java.time.LocalDateTime;

public record NotificationResource(Long id, String title, String message, String type, boolean isRead, LocalDateTime createdAt, UserSummaryResource user) {
    public record UserSummaryResource(Long id, String name) {}
}
