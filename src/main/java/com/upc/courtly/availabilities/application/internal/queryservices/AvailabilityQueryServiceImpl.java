package com.upc.courtly.availabilities.application.internal.queryservices;

import com.upc.courtly.availabilities.domain.model.aggregates.Availability;
import com.upc.courtly.availabilities.domain.model.queries.GetAllAvailabilitiesQuery;
import com.upc.courtly.availabilities.domain.model.queries.GetAvailabilityByIdQuery;
import com.upc.courtly.availabilities.domain.services.AvailabilityQueryService;
import com.upc.courtly.availabilities.infrastructure.persistence.jpa.repositories.AvailabilityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AvailabilityQueryServiceImpl implements AvailabilityQueryService {
    private final AvailabilityRepository availabilityRepository;

    public AvailabilityQueryServiceImpl(AvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
    }

    @Override
    public List<Availability> handle(GetAllAvailabilitiesQuery query) {
        return availabilityRepository.findAll();
    }

    @Override
    public Optional<Availability> handle(GetAvailabilityByIdQuery query) {
        return availabilityRepository.findById(query.availabilityId());
    }

    @Override
    public List<Availability> handleByCoachUserId(Long userId) {
        return availabilityRepository.findByCoachUserIdOrderByDateAscStartTimeAsc(userId);
    }
}
