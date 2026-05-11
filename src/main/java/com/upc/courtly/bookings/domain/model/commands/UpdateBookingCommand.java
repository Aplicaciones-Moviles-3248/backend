package com.upc.courtly.bookings.domain.model.commands;

import com.upc.courtly.bookings.domain.model.valueobjects.BookingStatus;

import java.time.LocalDateTime;

public record UpdateBookingCommand(Long bookingId, LocalDateTime startTime, LocalDateTime endTime, BookingStatus status) {
}

