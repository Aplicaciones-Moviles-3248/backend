package com.upc.courtly.reviews.domain.model.commands;

import com.upc.courtly.reviews.domain.model.valueobjects.ReviewTargetType;

public record UpdateReviewCommand(Long reviewId, Integer score, String comment, String type, Long targetId, ReviewTargetType targetType) {
}
