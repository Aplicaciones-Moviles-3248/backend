package com.upc.courtly.bookings.domain.model.commands;

import java.time.LocalDateTime;

public record UpdateBookingCommand(Long bookingId, LocalDateTime startTime, LocalDateTime endTime) {
}

