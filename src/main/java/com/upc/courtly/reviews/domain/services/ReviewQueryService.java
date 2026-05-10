package com.upc.courtly.reviews.domain.services;

import com.upc.courtly.reviews.domain.model.aggregates.Review;
import com.upc.courtly.reviews.domain.model.queries.GetAllReviewsQuery;
import com.upc.courtly.reviews.domain.model.queries.GetReviewByIdQuery;

import java.util.List;
import java.util.Optional;

public interface ReviewQueryService {
    List<Review> handle(GetAllReviewsQuery query);
    Optional<Review> handle(GetReviewByIdQuery query);
}
