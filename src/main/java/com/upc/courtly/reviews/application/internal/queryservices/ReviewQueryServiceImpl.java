package com.upc.courtly.reviews.application.internal.queryservices;

import com.upc.courtly.reviews.domain.model.aggregates.Review;
import com.upc.courtly.reviews.domain.model.queries.GetAllReviewsQuery;
import com.upc.courtly.reviews.domain.model.queries.GetReviewByIdQuery;
import com.upc.courtly.reviews.domain.services.ReviewQueryService;
import com.upc.courtly.reviews.infrastructure.persistence.jpa.repositories.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewQueryServiceImpl implements ReviewQueryService {
    private final ReviewRepository reviewRepository;

    public ReviewQueryServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public List<Review> handle(GetAllReviewsQuery query) {
        return reviewRepository.findAll();
    }

    @Override
    public Optional<Review> handle(GetReviewByIdQuery query) {
        return reviewRepository.findById(query.reviewId());
    }
}
