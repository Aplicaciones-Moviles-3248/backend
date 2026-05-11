package com.upc.courtly.trainingsessions.interfaces.rest.resources;

import com.upc.courtly.trainingsessions.domain.model.valueobjects.TrainingSessionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TrainingSessionResource(Long id, LocalDateTime startTime, LocalDateTime endTime,
                                      TrainingSessionStatus status, BigDecimal price,
                                      UserSummaryResource player, CoachSummaryResource coach,
                                      CourtSummaryResource court, Long availabilityId) {
    public record UserSummaryResource(Long id, String name) {}
    public record CoachSummaryResource(Long id, String name, Long userId) {}
    public record CourtSummaryResource(Long id, String name) {}
}
