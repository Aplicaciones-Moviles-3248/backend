package com.upc.courtly.trainingsessions.domain.model.commands;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateTrainingSessionCommand(Long playerId, Long coachId, Long courtId, Long availabilityId,
                                           LocalDateTime startTime, LocalDateTime endTime, BigDecimal price) {
}
