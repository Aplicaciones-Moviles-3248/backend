package com.upc.courtly.reviews.interfaces.rest.resources;

public record CreateReviewResource(Integer score, String comment, String type, Long targetId, String targetType, Long userId) {
}
