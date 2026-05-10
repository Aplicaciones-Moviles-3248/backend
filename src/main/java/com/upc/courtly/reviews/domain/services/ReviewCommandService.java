package com.upc.courtly.reviews.domain.services;

import com.upc.courtly.reviews.domain.model.aggregates.Review;
import com.upc.courtly.reviews.domain.model.commands.CreateReviewCommand;
import com.upc.courtly.reviews.domain.model.commands.DeleteReviewCommand;
import com.upc.courtly.reviews.domain.model.commands.UpdateReviewCommand;

import java.util.Optional;

public interface ReviewCommandService {
    Optional<Review> handle(CreateReviewCommand command);
    Optional<Review> handle(UpdateReviewCommand command);
    void handle(DeleteReviewCommand command);
}
