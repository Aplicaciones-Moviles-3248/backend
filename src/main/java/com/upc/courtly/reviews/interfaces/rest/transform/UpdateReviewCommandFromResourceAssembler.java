package com.upc.courtly.reviews.interfaces.rest.transform;

import com.upc.courtly.reviews.domain.model.commands.UpdateReviewCommand;
import com.upc.courtly.reviews.domain.model.valueobjects.ReviewTargetType;
import com.upc.courtly.reviews.interfaces.rest.resources.UpdateReviewResource;

public class UpdateReviewCommandFromResourceAssembler {
    public static UpdateReviewCommand toCommandFromResource(Long reviewId, UpdateReviewResource resource) {
        return new UpdateReviewCommand(
                reviewId,
                resource.score(),
                resource.comment(),
                resource.type(),
                resource.targetId(),
                ReviewTargetType.valueOf(resource.targetType())
        );
    }
}
