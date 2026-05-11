package com.upc.courtly.trainingsessions.domain.model.commands;

public record CancelTrainingSessionCommand(Long trainingSessionId, String reason) {
}
