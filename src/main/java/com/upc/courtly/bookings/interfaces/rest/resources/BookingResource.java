package com.upc.courtly.bookings.interfaces.rest.resources;

import com.upc.courtly.bookings.domain.model.valueobjects.BookingStatus;

import java.time.LocalDateTime;

public record BookingResource(Long id, LocalDateTime startTime, LocalDateTime endTime, BookingStatus status, UserSummaryResource user, CourtSummaryResource court) {
    public record UserSummaryResource(Long id, String name) {}
    public record CourtSummaryResource(Long id, String name) {}
}

