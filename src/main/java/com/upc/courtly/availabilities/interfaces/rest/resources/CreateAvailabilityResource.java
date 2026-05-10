package com.upc.courtly.availabilities.interfaces.rest.resources;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateAvailabilityResource(LocalDate date, LocalTime startTime, LocalTime endTime, String status, Long coachId) {
}
