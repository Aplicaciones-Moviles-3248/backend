package com.upc.courtly.bookings.domain.services;

import com.upc.courtly.bookings.domain.model.aggregates.Booking;
import com.upc.courtly.bookings.domain.model.queries.GetAllBookingsQuery;
import com.upc.courtly.bookings.domain.model.queries.GetBookingByIdQuery;
import java.util.List;
import java.util.Optional;

public interface BookingQueryService {
    List<Booking> handle(GetAllBookingsQuery query);
    Optional<Booking> handle(GetBookingByIdQuery query);
}

