package com.upc.courtly.availabilities.domain.services;

import com.upc.courtly.availabilities.domain.model.aggregates.Availability;
import com.upc.courtly.availabilities.domain.model.queries.GetAllAvailabilitiesQuery;
import com.upc.courtly.availabilities.domain.model.queries.GetAvailabilityByIdQuery;

import java.util.List;
import java.util.Optional;

public interface AvailabilityQueryService {
    List<Availability> handle(GetAllAvailabilitiesQuery query);
    Optional<Availability> handle(GetAvailabilityByIdQuery query);
    List<Availability> handleByCoachUserId(Long userId);
}
