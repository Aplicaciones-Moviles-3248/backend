package com.upc.courtly.bookings.domain.model.commands;

import java.time.LocalDateTime;

public record CreateBookingCommand(LocalDateTime startTime, LocalDateTime endTime, Long userId, Long courtId) {
}

