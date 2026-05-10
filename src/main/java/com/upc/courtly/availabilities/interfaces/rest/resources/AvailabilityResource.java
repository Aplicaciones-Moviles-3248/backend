package com.upc.courtly.availabilities.interfaces.rest.resources;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record AvailabilityResource(Long id, LocalDate date, LocalTime startTime, LocalTime endTime, String status, LocalDateTime createdAt, CoachSummaryResource coach) {
    public record CoachSummaryResource(Long id, String name) {}
}
