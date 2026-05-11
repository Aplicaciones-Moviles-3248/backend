package com.upc.courtly.trainingsessions.domain.model.commands;

public record RejectTrainingSessionCommand(Long trainingSessionId, String reason) {
}
