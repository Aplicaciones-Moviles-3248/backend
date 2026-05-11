package com.upc.courtly.reviews.application.internal.commandservices;

import com.upc.courtly.bookings.domain.model.valueobjects.BookingStatus;
import com.upc.courtly.bookings.infrastructure.persistence.jpa.repositories.BookingRepository;
import com.upc.courtly.reviews.domain.model.aggregates.Review;
import com.upc.courtly.reviews.domain.model.commands.CreateReviewCommand;
import com.upc.courtly.reviews.domain.model.commands.DeleteReviewCommand;
import com.upc.courtly.reviews.domain.model.commands.UpdateReviewCommand;
import com.upc.courtly.reviews.domain.model.valueobjects.ReviewTargetType;
import com.upc.courtly.reviews.domain.services.ReviewCommandService;
import com.upc.courtly.reviews.infrastructure.persistence.jpa.repositories.ReviewRepository;
import com.upc.courtly.trainingsessions.domain.model.valueobjects.TrainingSessionStatus;
import com.upc.courtly.trainingsessions.infrastructure.persistence.jpa.repositories.TrainingSessionRepository;
import com.upc.courtly.users.infrastructure.persistence.jpa.repositories.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReviewCommandServiceImpl implements ReviewCommandService {
    private final ReviewRepository reviewRepository;
    private final UserProfileRepository userProfileRepository;
    private final BookingRepository bookingRepository;
    private final TrainingSessionRepository trainingSessionRepository;

    public ReviewCommandServiceImpl(ReviewRepository reviewRepository, UserProfileRepository userProfileRepository,
                                    BookingRepository bookingRepository, TrainingSessionRepository trainingSessionRepository) {
        this.reviewRepository = reviewRepository;
        this.userProfileRepository = userProfileRepository;
        this.bookingRepository = bookingRepository;
        this.trainingSessionRepository = trainingSessionRepository;
    }

    @Override
    public Optional<Review> handle(CreateReviewCommand command) {
        var user = userProfileRepository.findById(command.userId()).orElseThrow(() -> new IllegalArgumentException("User with id " + command.userId() + " not found"));
        if (command.bookingId() == null && command.trainingSessionId() == null) {
            throw new IllegalArgumentException("Review must reference a completed booking or training session");
        }
        if (command.bookingId() != null) {
            var booking = bookingRepository.findById(command.bookingId()).orElseThrow(() -> new IllegalArgumentException("Booking with id " + command.bookingId() + " not found"));
            if (!booking.getUser().getId().equals(command.userId()) || booking.getStatus() != BookingStatus.COMPLETED) {
                throw new IllegalArgumentException("Only the player who completed the booking can review this court");
            }
            if (command.targetType() != ReviewTargetType.COURT || !booking.getCourt().getId().equals(command.targetId())) {
                throw new IllegalArgumentException("Booking reviews must target the completed court");
            }
        }
        if (command.trainingSessionId() != null) {
            var trainingSession = trainingSessionRepository.findById(command.trainingSessionId()).orElseThrow(() -> new IllegalArgumentException("Training session with id " + command.trainingSessionId() + " not found"));
            if (!trainingSession.getPlayer().getId().equals(command.userId()) || trainingSession.getStatus() != TrainingSessionStatus.COMPLETED) {
                throw new IllegalArgumentException("Only the player who completed the training session can submit this review");
            }
            boolean validTarget = (command.targetType() == ReviewTargetType.COACH && trainingSession.getCoach().getId().equals(command.targetId()))
                    || (command.targetType() == ReviewTargetType.COURT && trainingSession.getCourt().getId().equals(command.targetId()));
            if (!validTarget) {
                throw new IllegalArgumentException("Training session review target does not match the completed service");
            }
        }
        var review = new Review(command.score(), command.comment(), command.type(), command.targetId(),
                command.targetType(), command.bookingId(), command.trainingSessionId(), user);
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
