package com.upc.courtly.bookings.interfaces.rest.transform;

import com.upc.courtly.bookings.domain.model.commands.UpdateBookingCommand;
import com.upc.courtly.bookings.domain.model.valueobjects.BookingStatus;
import com.upc.courtly.bookings.interfaces.rest.resources.UpdateBookingResource;

public class UpdateBookingCommandFromResourceAssembler {
    public static UpdateBookingCommand toCommandFromResource(Long bookingId, UpdateBookingResource resource) {
        return new UpdateBookingCommand(
                bookingId,
                resource.startTime(),
                resource.endTime(),
                resource.status() != null ? BookingStatus.valueOf(resource.status()) : null
        );
    }
}

