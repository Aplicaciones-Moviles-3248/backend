package com.upc.courtly.bookings.interfaces.rest;

import com.upc.courtly.bookings.domain.model.commands.CancelBookingCommand;
import com.upc.courtly.bookings.domain.model.commands.CompleteBookingCommand;
import com.upc.courtly.bookings.domain.model.commands.DeleteBookingCommand;
import com.upc.courtly.bookings.domain.model.queries.GetAllBookingsQuery;
import com.upc.courtly.bookings.domain.model.queries.GetBookingByIdQuery;
import com.upc.courtly.bookings.domain.services.BookingCommandService;
import com.upc.courtly.bookings.domain.services.BookingQueryService;
import com.upc.courtly.bookings.interfaces.rest.resources.BookingResource;
import com.upc.courtly.bookings.interfaces.rest.resources.CreateBookingResource;
import com.upc.courtly.bookings.interfaces.rest.resources.UpdateBookingResource;
import com.upc.courtly.bookings.interfaces.rest.transform.BookingResourceFromEntityAssembler;
import com.upc.courtly.bookings.interfaces.rest.transform.CreateBookingCommandFromResourceAssembler;
import com.upc.courtly.bookings.interfaces.rest.transform.UpdateBookingCommandFromResourceAssembler;
import com.upc.courtly.iam.interfaces.acl.AuthenticatedContextFacade;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/bookings", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Bookings", description = "Booking Management Endpoints")
public class BookingsController {
    private final BookingCommandService bookingCommandService;
    private final BookingQueryService bookingQueryService;
    private final AuthenticatedContextFacade authenticatedContextFacade;

    public BookingsController(BookingCommandService bookingCommandService, BookingQueryService bookingQueryService,
                              AuthenticatedContextFacade authenticatedContextFacade) {
        this.bookingCommandService = bookingCommandService;
        this.bookingQueryService = bookingQueryService;
        this.authenticatedContextFacade = authenticatedContextFacade;
    }

    @PostMapping
    public ResponseEntity<BookingResource> createBooking(@RequestBody CreateBookingResource resource) {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        if (!currentUserProfile.getId().equals(resource.userId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only create bookings for your own profile");
        }
        var command = CreateBookingCommandFromResourceAssembler.toCommandFromResource(resource);
        var booking = bookingCommandService.handle(command);
        return booking.map(b -> new ResponseEntity<>(BookingResourceFromEntityAssembler.toResourceFromEntity(b), HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping
    public ResponseEntity<List<BookingResource>> getAllBookings() {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        var query = new GetAllBookingsQuery();
        var bookings = bookingQueryService.handle(query);
        var bookingResources = bookings.stream()
                .filter(booking -> booking.getUser().getId().equals(currentUserProfile.getId()))
                .map(BookingResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(bookingResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResource> getBookingById(@PathVariable Long id) {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        var query = new GetBookingByIdQuery(id);
        var booking = bookingQueryService.handle(query);
        booking.ifPresent(value -> {
            if (!value.getUser().getId().equals(currentUserProfile.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own bookings");
            }
        });
        return booking.map(b -> ResponseEntity.ok(BookingResourceFromEntityAssembler.toResourceFromEntity(b)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingResource> updateBooking(@PathVariable Long id, @RequestBody UpdateBookingResource resource) {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        var existingBooking = bookingQueryService.handle(new GetBookingByIdQuery(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        if (!existingBooking.getUser().getId().equals(currentUserProfile.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own bookings");
        }
        var command = UpdateBookingCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var updatedBooking = bookingCommandService.handle(command);
        return updatedBooking.map(b -> ResponseEntity.ok(BookingResourceFromEntityAssembler.toResourceFromEntity(b)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingResource> cancelBooking(@PathVariable Long id) {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        var existingBooking = bookingQueryService.handle(new GetBookingByIdQuery(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        if (!existingBooking.getUser().getId().equals(currentUserProfile.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only cancel your own bookings");
        }
        var cancelledBooking = bookingCommandService.handle(new CancelBookingCommand(id));
        return cancelledBooking.map(b -> ResponseEntity.ok(BookingResourceFromEntityAssembler.toResourceFromEntity(b)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<BookingResource> completeBooking(@PathVariable Long id) {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        var existingBooking = bookingQueryService.handle(new GetBookingByIdQuery(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        if (!existingBooking.getUser().getId().equals(currentUserProfile.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only complete your own bookings");
        }
        var completedBooking = bookingCommandService.handle(new CompleteBookingCommand(id));
        return completedBooking.map(b -> ResponseEntity.ok(BookingResourceFromEntityAssembler.toResourceFromEntity(b)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        var existingBooking = bookingQueryService.handle(new GetBookingByIdQuery(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        if (!existingBooking.getUser().getId().equals(currentUserProfile.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own bookings");
        }
        var command = new DeleteBookingCommand(id);
        bookingCommandService.handle(command);
        return ResponseEntity.ok("Booking deleted successfully.");
    }
}
