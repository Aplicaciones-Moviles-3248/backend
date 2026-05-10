package com.upc.courtly.availabilities.domain.model.aggregates;

import com.upc.courtly.availabilities.domain.model.valueobjects.AvailabilityStatus;
import com.upc.courtly.coaches.domain.model.aggregates.Coach;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "availabilities")
@Getter
@Setter
@NoArgsConstructor
public class Availability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AvailabilityStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id", nullable = false)
    private Coach coach;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Availability(LocalDate date, LocalTime startTime, LocalTime endTime, AvailabilityStatus status, Coach coach) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.coach = coach;
    }

    public void updateAvailability(LocalDate date, LocalTime startTime, LocalTime endTime, AvailabilityStatus status) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }
}
