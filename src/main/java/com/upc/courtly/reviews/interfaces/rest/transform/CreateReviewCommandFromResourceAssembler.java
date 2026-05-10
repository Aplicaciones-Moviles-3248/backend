package com.upc.courtly.reviews.interfaces.rest.transform;

import com.upc.courtly.reviews.domain.model.commands.CreateReviewCommand;
import com.upc.courtly.reviews.domain.model.valueobjects.ReviewTargetType;
import com.upc.courtly.reviews.interfaces.rest.resources.CreateReviewResource;

public class CreateReviewCommandFromResourceAssembler {
    public static CreateReviewCommand toCommandFromResource(CreateReviewResource resource) {
        return new CreateReviewCommand(
                resource.score(),
                resource.comment(),
                resource.type(),
                resource.targetId(),
                ReviewTargetType.valueOf(resource.targetType()),
                resource.userId()
        );
    }
}
