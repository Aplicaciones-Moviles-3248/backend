package com.upc.courtly.reviews.interfaces.rest.resources;

public record UpdateReviewResource(Integer score, String comment, String type, Long targetId, String targetType) {
}
