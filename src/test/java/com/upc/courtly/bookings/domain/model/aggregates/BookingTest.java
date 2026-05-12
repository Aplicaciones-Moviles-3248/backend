package com.upc.courtly.bookings.domain.model.aggregates;

import com.upc.courtly.bookings.domain.model.valueobjects.BookingStatus;
import com.upc.courtly.courts.domain.model.aggregates.Court;
import com.upc.courtly.users.domain.model.aggregates.UserProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class BookingTest {

    @Test
    @DisplayName("Debería crear una reserva correctamente con estado inicial de pago pendiente")
    void shouldCreateBookingCorrectly() {

        UserProfile user = new UserProfile();
        Court court = new Court();

        LocalDateTime start = LocalDateTime.of(2026, 5, 10, 18, 0);
        LocalDateTime end = LocalDateTime.of(2026, 5, 10, 19, 0);

        Booking booking = new Booking(start, end, user, court);

        assertNotNull(booking);
        assertEquals(start, booking.getStartTime());
        assertEquals(end, booking.getEndTime());
        assertEquals(user, booking.getUser());
        assertEquals(court, booking.getCourt());
        assertEquals(BookingStatus.PENDING_PAYMENT, booking.getStatus());
    }

    @Test
    @DisplayName("Debería actualizar correctamente el horario de una reserva")
    void shouldUpdateBookingCorrectly() {

        UserProfile user = new UserProfile();
        Court court = new Court();

        Booking booking = new Booking(
                LocalDateTime.of(2026, 5, 10, 18, 0),
                LocalDateTime.of(2026, 5, 10, 19, 0),
                user,
                court
        );

        LocalDateTime newStart = LocalDateTime.of(2026, 5, 11, 20, 0);
        LocalDateTime newEnd = LocalDateTime.of(2026, 5, 11, 21, 0);

        booking.updateBooking(newStart, newEnd);

        assertEquals(newStart, booking.getStartTime());
        assertEquals(newEnd, booking.getEndTime());
    }

    @Test
    @DisplayName("Debería confirmar correctamente una reserva")
    void shouldConfirmBookingCorrectly() {

        UserProfile user = new UserProfile();
        Court court = new Court();

        Booking booking = new Booking(
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                user,
                court
        );

        booking.confirm();

        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
    }

    @Test
    @DisplayName("Debería cancelar correctamente una reserva")
    void shouldCancelBookingCorrectly() {

        UserProfile user = new UserProfile();
        Court court = new Court();

        Booking booking = new Booking(
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                user,
                court
        );

        booking.cancel();

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
    }

    @Test
    @DisplayName("Debería completar correctamente una reserva")
    void shouldCompleteBookingCorrectly() {

        UserProfile user = new UserProfile();
        Court court = new Court();

        Booking booking = new Booking(
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                user,
                court
        );

        booking.complete();

        assertEquals(BookingStatus.COMPLETED, booking.getStatus());
    }

    @Test
    @DisplayName("Debería generar createdAt automáticamente al persistirse")
    void shouldGenerateCreatedAtOnPersist() {

        Booking booking = new Booking();

        booking.onCreate();

        assertNotNull(booking.getCreatedAt());
    }
}