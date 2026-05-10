package com.upc.courtly.reviews.interfaces.rest.resources;

import java.time.LocalDateTime;

public record ReviewResource(Long id, Integer score, String comment, String type, Long targetId, String targetType, LocalDateTime createdAt, UserSummaryResource user) {
    public record UserSummaryResource(Long id, String name) {}
}
