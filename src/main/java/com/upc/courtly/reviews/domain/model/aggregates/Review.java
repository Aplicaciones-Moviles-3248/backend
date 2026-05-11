package com.upc.courtly.reviews.domain.model.aggregates;

import com.upc.courtly.reviews.domain.model.valueobjects.ReviewTargetType;
import com.upc.courtly.users.domain.model.aggregates.UserProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewTargetType targetType;

    @Column
    private Long bookingId;

    @Column
    private Long trainingSessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile user;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Review(Integer score, String comment, String type, Long targetId, ReviewTargetType targetType,
                  Long bookingId, Long trainingSessionId, UserProfile user) {
        this.score = score;
        this.comment = comment;
        this.type = type;
        this.targetId = targetId;
        this.targetType = targetType;
        this.bookingId = bookingId;
        this.trainingSessionId = trainingSessionId;
        this.user = user;
    }

    public void updateReview(Integer score, String comment, String type, Long targetId, ReviewTargetType targetType) {
        this.score = score;
        this.comment = comment;
        this.type = type;
        this.targetId = targetId;
        this.targetType = targetType;
    }
}
