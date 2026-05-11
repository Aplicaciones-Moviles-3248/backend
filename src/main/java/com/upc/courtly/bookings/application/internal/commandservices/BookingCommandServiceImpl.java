package com.upc.courtly.bookings.application.internal.commandservices;

import com.upc.courtly.bookings.domain.model.aggregates.Booking;
import com.upc.courtly.bookings.domain.model.commands.CancelBookingCommand;
import com.upc.courtly.bookings.domain.model.commands.CompleteBookingCommand;
import com.upc.courtly.bookings.domain.model.commands.CreateBookingCommand;
import com.upc.courtly.bookings.domain.model.commands.DeleteBookingCommand;
import com.upc.courtly.bookings.domain.model.commands.UpdateBookingCommand;
import com.upc.courtly.bookings.domain.model.valueobjects.BookingStatus;
import com.upc.courtly.bookings.domain.services.BookingCommandService;
import com.upc.courtly.bookings.infrastructure.persistence.jpa.repositories.BookingRepository;
import com.upc.courtly.courts.infrastructure.persistence.jpa.repositories.CourtRepository;
import com.upc.courtly.notifications.domain.model.aggregates.Notification;
import com.upc.courtly.notifications.domain.model.valueobjects.NotificationType;
import com.upc.courtly.notifications.infrastructure.persistence.jpa.repositories.NotificationRepository;
import com.upc.courtly.users.infrastructure.persistence.jpa.repositories.UserProfileRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class BookingCommandServiceImpl implements BookingCommandService {
    private final BookingRepository bookingRepository;
    private final UserProfileRepository userProfileRepository;
    private final CourtRepository courtRepository;
    private final NotificationRepository notificationRepository;

    public BookingCommandServiceImpl(BookingRepository bookingRepository, UserProfileRepository userProfileRepository,
                                     CourtRepository courtRepository, NotificationRepository notificationRepository) {
        this.bookingRepository = bookingRepository;
        this.userProfileRepository = userProfileRepository;
        this.courtRepository = courtRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Optional<Booking> handle(CreateBookingCommand command) {
        var user = userProfileRepository.findById(command.userId()).orElseThrow(() -> new IllegalArgumentException("User with id " + command.userId() + " not found"));
        var court = courtRepository.findById(command.courtId()).orElseThrow(() -> new IllegalArgumentException("Court with id " + command.courtId() + " not found"));
        if (bookingRepository.existsOverlappingBooking(command.courtId(), command.startTime(), command.endTime(),
                java.util.List.of(BookingStatus.PENDING_PAYMENT, BookingStatus.CONFIRMED))) {
            throw new IllegalArgumentException("Court is already booked for the requested time range");
        }
        var booking = new Booking(command.startTime(), command.endTime(), user, court);
        var createdBooking = bookingRepository.save(booking);
        notificationRepository.save(new Notification(
                "Booking created",
                "Your court booking was created and is waiting for payment confirmation.",
                NotificationType.BOOKING_CREATED,
                false,
                "BOOKING",
                createdBooking.getId(),
                user
        ));
        return Optional.of(createdBooking);
    }

    @Override
    public Optional<Booking> handle(UpdateBookingCommand command) {
        return bookingRepository.findById(command.bookingId()).map(bookingToUpdate -> {
            bookingToUpdate.updateBooking(command.startTime(), command.endTime());
            if (command.status() != null) {
                switch (command.status()) {
                    case CONFIRMED -> bookingToUpdate.confirm();
                    case CANCELLED -> bookingToUpdate.cancel();
                    case COMPLETED -> bookingToUpdate.complete();
                    case PENDING_PAYMENT -> bookingToUpdate.setStatus(BookingStatus.PENDING_PAYMENT);
                }
            }
            return bookingRepository.save(bookingToUpdate);
        });
    }

    @Override
    public Optional<Booking> handle(CancelBookingCommand command) {
        return bookingRepository.findById(command.bookingId()).map(booking -> {
            booking.cancel();
            var savedBooking = bookingRepository.save(booking);
            notificationRepository.save(new Notification(
                    "Booking cancelled",
                    "Your booking was cancelled successfully.",
                    NotificationType.BOOKING_CANCELLED,
                    false,
                    "BOOKING",
                    savedBooking.getId(),
                    savedBooking.getUser()
            ));
            return savedBooking;
        });
    }

    @Override
    public Optional<Booking> handle(CompleteBookingCommand command) {
        return bookingRepository.findById(command.bookingId()).map(booking -> {
            booking.complete();
            return bookingRepository.save(booking);
        });
    }

    @Override
    public void handle(DeleteBookingCommand command) {
        if (!bookingRepository.existsById(command.bookingId())) {
            throw new IllegalArgumentException("Booking with id " + command.bookingId() + " not found");
        }
        bookingRepository.deleteById(command.bookingId());
    }
}

