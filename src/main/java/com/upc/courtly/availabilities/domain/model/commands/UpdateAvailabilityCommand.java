package com.upc.courtly.availabilities.domain.model.commands;

import com.upc.courtly.availabilities.domain.model.valueobjects.AvailabilityStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record UpdateAvailabilityCommand(Long availabilityId, LocalDate date, LocalTime startTime, LocalTime endTime, AvailabilityStatus status) {
}
