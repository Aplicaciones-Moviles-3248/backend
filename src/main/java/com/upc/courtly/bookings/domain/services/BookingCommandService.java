package com.upc.courtly.bookings.domain.services;

import com.upc.courtly.bookings.domain.model.aggregates.Booking;
import com.upc.courtly.bookings.domain.model.commands.CancelBookingCommand;
import com.upc.courtly.bookings.domain.model.commands.CompleteBookingCommand;
import com.upc.courtly.bookings.domain.model.commands.CreateBookingCommand;
import com.upc.courtly.bookings.domain.model.commands.DeleteBookingCommand;
import com.upc.courtly.bookings.domain.model.commands.UpdateBookingCommand;
import java.util.Optional;

public interface BookingCommandService {
    Optional<Booking> handle(CreateBookingCommand command);
    Optional<Booking> handle(UpdateBookingCommand command);
    Optional<Booking> handle(CancelBookingCommand command);
    Optional<Booking> handle(CompleteBookingCommand command);
    void handle(DeleteBookingCommand command);
}

