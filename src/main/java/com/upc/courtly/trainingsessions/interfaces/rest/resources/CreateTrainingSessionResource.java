package com.upc.courtly.trainingsessions.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateTrainingSessionResource(Long playerId, Long coachId, Long courtId, Long availabilityId,
                                            LocalDateTime startTime, LocalDateTime endTime, BigDecimal price) {
}
