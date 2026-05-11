package com.upc.courtly.payments.domain.model.commands;

public record CreatePaymentCommand(Long userId, Long bookingId, Long trainingSessionId) {
}

