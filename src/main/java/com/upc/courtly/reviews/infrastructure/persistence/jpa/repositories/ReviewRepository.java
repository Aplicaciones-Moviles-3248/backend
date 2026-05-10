package com.upc.courtly.reviews.infrastructure.persistence.jpa.repositories;

import com.upc.courtly.reviews.domain.model.aggregates.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
}
