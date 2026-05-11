package com.upc.courtly.reviews.interfaces.rest;

import com.upc.courtly.iam.interfaces.acl.AuthenticatedContextFacade;
import com.upc.courtly.reviews.domain.model.commands.DeleteReviewCommand;
import com.upc.courtly.reviews.domain.model.queries.GetAllReviewsQuery;
import com.upc.courtly.reviews.domain.model.queries.GetReviewByIdQuery;
import com.upc.courtly.reviews.domain.services.ReviewCommandService;
import com.upc.courtly.reviews.domain.services.ReviewQueryService;
import com.upc.courtly.reviews.interfaces.rest.resources.CreateReviewResource;
import com.upc.courtly.reviews.interfaces.rest.resources.ReviewResource;
import com.upc.courtly.reviews.interfaces.rest.resources.UpdateReviewResource;
import com.upc.courtly.reviews.interfaces.rest.transform.CreateReviewCommandFromResourceAssembler;
import com.upc.courtly.reviews.interfaces.rest.transform.ReviewResourceFromEntityAssembler;
import com.upc.courtly.reviews.interfaces.rest.transform.UpdateReviewCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/reviews", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Reviews", description = "Review Management Endpoints")
public class ReviewsController {
    private final ReviewCommandService reviewCommandService;
    private final ReviewQueryService reviewQueryService;
    private final AuthenticatedContextFacade authenticatedContextFacade;

    public ReviewsController(ReviewCommandService reviewCommandService, ReviewQueryService reviewQueryService,
                             AuthenticatedContextFacade authenticatedContextFacade) {
        this.reviewCommandService = reviewCommandService;
        this.reviewQueryService = reviewQueryService;
        this.authenticatedContextFacade = authenticatedContextFacade;
    }

    @PostMapping
    public ResponseEntity<ReviewResource> createReview(@RequestBody CreateReviewResource resource) {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        if (!currentUserProfile.getId().equals(resource.userId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only create reviews for your own profile");
        }
        var command = CreateReviewCommandFromResourceAssembler.toCommandFromResource(resource);
        var review = reviewCommandService.handle(command);
        return review.map(r -> new ResponseEntity<>(ReviewResourceFromEntityAssembler.toResourceFromEntity(r), HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping
    public ResponseEntity<List<ReviewResource>> getAllReviews() {
        var query = new GetAllReviewsQuery();
        var reviews = reviewQueryService.handle(query);
        var reviewResources = reviews.stream()
                .map(ReviewResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(reviewResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResource> getReviewById(@PathVariable Long id) {
        var query = new GetReviewByIdQuery(id);
        var review = reviewQueryService.handle(query);
        return review.map(r -> ResponseEntity.ok(ReviewResourceFromEntityAssembler.toResourceFromEntity(r)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResource> updateReview(@PathVariable Long id, @RequestBody UpdateReviewResource resource) {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        var existingReview = reviewQueryService.handle(new GetReviewByIdQuery(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
        if (!existingReview.getUser().getId().equals(currentUserProfile.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own reviews");
        }
        var command = UpdateReviewCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var updatedReview = reviewCommandService.handle(command);
        return updatedReview.map(r -> ResponseEntity.ok(ReviewResourceFromEntityAssembler.toResourceFromEntity(r)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        var existingReview = reviewQueryService.handle(new GetReviewByIdQuery(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
        if (!existingReview.getUser().getId().equals(currentUserProfile.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own reviews");
        }
        var command = new DeleteReviewCommand(id);
        reviewCommandService.handle(command);
        return ResponseEntity.ok("Review deleted successfully.");
    }
}
