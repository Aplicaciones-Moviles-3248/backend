package com.upc.courtly.bookings.interfaces.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upc.courtly.bookings.domain.model.aggregates.Booking;
import com.upc.courtly.bookings.domain.model.commands.CancelBookingCommand;
import com.upc.courtly.bookings.domain.model.commands.CompleteBookingCommand;
import com.upc.courtly.bookings.domain.model.commands.CreateBookingCommand;
import com.upc.courtly.bookings.domain.model.commands.DeleteBookingCommand;
import com.upc.courtly.bookings.domain.model.commands.UpdateBookingCommand;
import com.upc.courtly.bookings.domain.model.queries.GetAllBookingsQuery;
import com.upc.courtly.bookings.domain.model.queries.GetBookingByIdQuery;
import com.upc.courtly.bookings.domain.model.valueobjects.BookingStatus;
import com.upc.courtly.bookings.domain.services.BookingCommandService;
import com.upc.courtly.bookings.domain.services.BookingQueryService;
import com.upc.courtly.bookings.interfaces.rest.resources.CreateBookingResource;
import com.upc.courtly.iam.interfaces.acl.AuthenticatedContextFacade;
import com.upc.courtly.courts.domain.model.aggregates.Court;
import com.upc.courtly.users.domain.model.aggregates.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BookingsIntegrationTest {

    @Mock
    private BookingCommandService bookingCommandService;

    @Mock
    private BookingQueryService bookingQueryService;

    @Mock
    private AuthenticatedContextFacade authenticatedContextFacade;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new BookingsController(
                        bookingCommandService,
                        bookingQueryService,
                        authenticatedContextFacade
                ))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    private UserProfile buildCurrentUserProfile(Long id) {
        UserProfile profile = new UserProfile();
        ReflectionTestUtils.setField(profile, "id", id);
        return profile;
    }

    private Booking buildBooking(Long bookingId, Long userId, Long courtId) {
        UserProfile user = new UserProfile();
        ReflectionTestUtils.setField(user, "id", userId);

        Court court = new Court();
        ReflectionTestUtils.setField(court, "id", courtId);
        ReflectionTestUtils.setField(court, "name", "Court Central");

        Booking booking = new Booking(
                LocalDateTime.of(2026, 5, 10, 18, 0),
                LocalDateTime.of(2026, 5, 10, 19, 0),
                user,
                court
        );

        ReflectionTestUtils.setField(booking, "id", bookingId);
        ReflectionTestUtils.setField(booking, "status", BookingStatus.PENDING_PAYMENT);

        return booking;
    }

    @Test
    @DisplayName("Debería crear una reserva correctamente")
    void shouldCreateBookingSuccessfully() throws Exception {
        var currentUserProfile = buildCurrentUserProfile(1L);
        when(authenticatedContextFacade.getAuthenticatedUserProfile()).thenReturn(currentUserProfile);

        CreateBookingResource request = new CreateBookingResource(
                LocalDateTime.of(2026, 5, 10, 18, 0),
                LocalDateTime.of(2026, 5, 10, 19, 0),
                1L,
                10L
        );

        Booking booking = buildBooking(1L, 1L, 10L);

        when(bookingCommandService.handle(any(CreateBookingCommand.class)))
                .thenReturn(Optional.of(booking));

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("PENDING_PAYMENT"));
    }

    @Test
    @DisplayName("Debería devolver las reservas del usuario autenticado")
    void shouldReturnAllBookingsForCurrentUser() throws Exception {
        var currentUserProfile = buildCurrentUserProfile(1L);
        when(authenticatedContextFacade.getAuthenticatedUserProfile()).thenReturn(currentUserProfile);

        Booking booking = buildBooking(1L, 1L, 10L);

        when(bookingQueryService.handle(any(GetAllBookingsQuery.class)))
                .thenReturn(List.of(booking));

        mockMvc.perform(get("/api/v1/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("PENDING_PAYMENT"));
    }

    @Test
    @DisplayName("Debería devolver una reserva por su id")
    void shouldReturnBookingById() throws Exception {
        var currentUserProfile = buildCurrentUserProfile(1L);
        when(authenticatedContextFacade.getAuthenticatedUserProfile()).thenReturn(currentUserProfile);

        Booking booking = buildBooking(1L, 1L, 10L);

        when(bookingQueryService.handle(any(GetBookingByIdQuery.class)))
                .thenReturn(Optional.of(booking));

        mockMvc.perform(get("/api/v1/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("PENDING_PAYMENT"));
    }

    @Test
    @DisplayName("Debería cancelar una reserva correctamente")
    void shouldCancelBookingSuccessfully() throws Exception {
        var currentUserProfile = buildCurrentUserProfile(1L);
        when(authenticatedContextFacade.getAuthenticatedUserProfile()).thenReturn(currentUserProfile);

        Booking existingBooking = buildBooking(1L, 1L, 10L);
        Booking cancelledBooking = buildBooking(1L, 1L, 10L);
        ReflectionTestUtils.setField(cancelledBooking, "status", BookingStatus.CANCELLED);

        when(bookingQueryService.handle(any(GetBookingByIdQuery.class)))
                .thenReturn(Optional.of(existingBooking));

        when(bookingCommandService.handle(any(CancelBookingCommand.class)))
                .thenReturn(Optional.of(cancelledBooking));

        mockMvc.perform(post("/api/v1/bookings/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    @DisplayName("Debería eliminar una reserva correctamente")
    void shouldDeleteBookingSuccessfully() throws Exception {
        var currentUserProfile = buildCurrentUserProfile(1L);
        when(authenticatedContextFacade.getAuthenticatedUserProfile()).thenReturn(currentUserProfile);

        Booking existingBooking = buildBooking(1L, 1L, 10L);

        when(bookingQueryService.handle(any(GetBookingByIdQuery.class)))
                .thenReturn(Optional.of(existingBooking));

        doNothing().when(bookingCommandService).handle(any(DeleteBookingCommand.class));

        mockMvc.perform(delete("/api/v1/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("\"Booking deleted successfully.\""));
    }
}