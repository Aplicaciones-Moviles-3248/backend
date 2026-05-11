package com.upc.courtly.payments.interfaces.rest.resources;

public record CreatePaymentResource(Long userId, Long bookingId, Long trainingSessionId) {
}

