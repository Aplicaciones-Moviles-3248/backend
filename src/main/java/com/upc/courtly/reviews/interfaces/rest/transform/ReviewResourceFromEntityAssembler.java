package com.upc.courtly.reviews.interfaces.rest.transform;

import com.upc.courtly.reviews.domain.model.aggregates.Review;
import com.upc.courtly.reviews.interfaces.rest.resources.ReviewResource;

public class ReviewResourceFromEntityAssembler {
    public static ReviewResource toResourceFromEntity(Review entity) {
        return new ReviewResource(
                entity.getId(),
                entity.getScore(),
                entity.getComment(),
                entity.getType(),
                entity.getTargetId(),
                entity.getTargetType().name(),
                entity.getBookingId(),
                entity.getTrainingSessionId(),
                entity.getCreatedAt(),
                new ReviewResource.UserSummaryResource(entity.getUser().getId(), entity.getUser().getName())
        );
    }
}
