package com.upc.courtly.reviews.application.internal.commandservices;

import com.upc.courtly.reviews.domain.model.aggregates.Review;
import com.upc.courtly.reviews.domain.model.commands.CreateReviewCommand;
import com.upc.courtly.reviews.domain.model.commands.DeleteReviewCommand;
import com.upc.courtly.reviews.domain.model.commands.UpdateReviewCommand;
import com.upc.courtly.reviews.domain.services.ReviewCommandService;
import com.upc.courtly.reviews.infrastructure.persistence.jpa.repositories.ReviewRepository;
import com.upc.courtly.users.infrastructure.persistence.jpa.repositories.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReviewCommandServiceImpl implements ReviewCommandService {
    private final ReviewRepository reviewRepository;
    private final UserProfileRepository userProfileRepository;

    public ReviewCommandServiceImpl(ReviewRepository reviewRepository, UserProfileRepository userProfileRepository) {
        this.reviewRepository = reviewRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public Optional<Review> handle(CreateReviewCommand command) {
        var user = userProfileRepository.findById(command.userId()).orElseThrow(() -> new IllegalArgumentException("User with id " + command.userId() + " not found"));
        var review = new Review(command.score(), command.comment(), command.type(), command.targetId(), command.targetType(), user);
        var createdReview = reviewRepository.save(review);
        return Optional.of(createdReview);
    }

    @Override
    public Optional<Review> handle(UpdateReviewCommand command) {
        return reviewRepository.findById(command.reviewId()).map(reviewToUpdate -> {
            reviewToUpdate.updateReview(command.score(), command.comment(), command.type(), command.targetId(), command.targetType());
            return reviewRepository.save(reviewToUpdate);
        });
    }

    @Override
    public void handle(DeleteReviewCommand command) {
        if (!reviewRepository.existsById(command.reviewId())) {
            throw new IllegalArgumentException("Review with id " + command.reviewId() + " not found");
        }
        reviewRepository.deleteById(command.reviewId());
    }
}
