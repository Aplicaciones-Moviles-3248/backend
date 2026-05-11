package com.upc.courtly.trainingsessions.domain.model.aggregates;

import com.upc.courtly.availabilities.domain.model.aggregates.Availability;
import com.upc.courtly.coaches.domain.model.aggregates.Coach;
import com.upc.courtly.courts.domain.model.aggregates.Court;
import com.upc.courtly.trainingsessions.domain.model.valueobjects.TrainingSessionStatus;
import com.upc.courtly.users.domain.model.aggregates.UserProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "training_sessions")
@Getter
@Setter
@NoArgsConstructor
public class TrainingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_user_id", nullable = false)
    private UserProfile player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id", nullable = false)
    private Coach coach;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "availability_id", nullable = false, unique = true)
    private Availability availability;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrainingSessionStatus status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column
    private String rejectionReason;

    @Column
    private String cancellationReason;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime acceptedAt;

    @Column
    private LocalDateTime cancelledAt;

    @Column
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public TrainingSession(UserProfile player, Coach coach, Court court, Availability availability,
                           LocalDateTime startTime, LocalDateTime endTime, BigDecimal price) {
        this.player = player;
        this.coach = coach;
        this.court = court;
        this.availability = availability;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.status = TrainingSessionStatus.PENDING;
    }

    public void accept() {
        this.status = TrainingSessionStatus.ACCEPTED;
        this.acceptedAt = LocalDateTime.now();
        this.rejectionReason = null;
    }

    public void reject(String rejectionReason) {
        this.status = TrainingSessionStatus.REJECTED;
        this.rejectionReason = rejectionReason;
    }

    public void cancel(String cancellationReason) {
        this.status = TrainingSessionStatus.CANCELLED;
        this.cancellationReason = cancellationReason;
        this.cancelledAt = LocalDateTime.now();
    }

    public void complete() {
        this.status = TrainingSessionStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }
}
