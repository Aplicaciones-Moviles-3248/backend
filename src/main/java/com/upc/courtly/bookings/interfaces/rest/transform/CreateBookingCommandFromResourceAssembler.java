package com.upc.courtly.bookings.interfaces.rest.transform;

import com.upc.courtly.bookings.domain.model.commands.CreateBookingCommand;
import com.upc.courtly.bookings.interfaces.rest.resources.CreateBookingResource;

public class CreateBookingCommandFromResourceAssembler {
    public static CreateBookingCommand toCommandFromResource(CreateBookingResource resource) {
        return new CreateBookingCommand(
                resource.startTime(),
                resource.endTime(),
                resource.userId(),
                resource.courtId()
        );
    }
}

