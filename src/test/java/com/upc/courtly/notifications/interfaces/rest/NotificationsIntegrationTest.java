package com.upc.courtly.notifications.interfaces.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upc.courtly.iam.interfaces.acl.AuthenticatedContextFacade;
import com.upc.courtly.notifications.domain.model.aggregates.Notification;
import com.upc.courtly.notifications.domain.model.commands.DeleteNotificationCommand;
import com.upc.courtly.notifications.domain.model.commands.MarkNotificationAsReadCommand;
import com.upc.courtly.notifications.domain.model.queries.CountUnreadNotificationsByUserIdQuery;
import com.upc.courtly.notifications.domain.model.queries.GetAllNotificationsQuery;
import com.upc.courtly.notifications.domain.model.queries.GetNotificationByIdQuery;
import com.upc.courtly.notifications.domain.model.queries.GetNotificationsByUserIdQuery;
import com.upc.courtly.notifications.domain.model.valueobjects.NotificationType;
import com.upc.courtly.notifications.domain.services.NotificationCommandService;
import com.upc.courtly.notifications.domain.services.NotificationQueryService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class NotificationsIntegrationTest {

    @Mock
    private NotificationCommandService notificationCommandService;

    @Mock
    private NotificationQueryService notificationQueryService;

    @Mock
    private AuthenticatedContextFacade authenticatedContextFacade;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new NotificationsController(
                        notificationCommandService,
                        notificationQueryService,
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

    private Notification buildNotification(Long id, Long userId, String title, String message, boolean isRead) {
        UserProfile user = buildCurrentUserProfile(userId);

        Notification notification = new Notification(
                title,
                message,
                NotificationType.values()[0],
                isRead,
                "Booking",
                1L,
                user
        );

        ReflectionTestUtils.setField(notification, "id", id);
        ReflectionTestUtils.setField(notification, "createdAt", LocalDateTime.now());

        return notification;
    }

    @Test
    @DisplayName("Debería devolver las notificaciones del usuario autenticado")
    void shouldReturnNotificationsForCurrentUser() throws Exception {
        var currentUser = buildCurrentUserProfile(1L);
        when(authenticatedContextFacade.getAuthenticatedUserProfile()).thenReturn(currentUser);

        Notification notification = buildNotification(1L, 1L, "Reserva confirmada", "Tu reserva fue creada", false);

        when(notificationQueryService.handle(any(GetNotificationsByUserIdQuery.class)))
                .thenReturn(List.of(notification));

        mockMvc.perform(get("/api/v1/notifications/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Reserva confirmada"));
    }

    @Test
    @DisplayName("Debería devolver una notificación por su id")
    void shouldReturnNotificationById() throws Exception {
        var currentUser = buildCurrentUserProfile(1L);
        when(authenticatedContextFacade.getAuthenticatedUserProfile()).thenReturn(currentUser);

        Notification notification = buildNotification(1L, 1L, "Reserva confirmada", "Tu reserva fue creada", false);

        when(notificationQueryService.handle(any(GetNotificationByIdQuery.class)))
                .thenReturn(Optional.of(notification));

        mockMvc.perform(get("/api/v1/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Reserva confirmada"));
    }

    @Test
    @DisplayName("Debería devolver las notificaciones de un usuario específico")
    void shouldReturnNotificationsByUserId() throws Exception {
        var currentUser = buildCurrentUserProfile(1L);
        when(authenticatedContextFacade.getAuthenticatedUserProfile()).thenReturn(currentUser);

        Notification notification = buildNotification(1L, 1L, "Reserva confirmada", "Tu reserva fue creada", false);

        when(notificationQueryService.handle(any(GetNotificationsByUserIdQuery.class)))
                .thenReturn(List.of(notification));

        mockMvc.perform(get("/api/v1/notifications/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Reserva confirmada"));
    }

    @Test
    @DisplayName("Debería devolver el número de notificaciones no leídas")
    void shouldReturnUnreadCount() throws Exception {
        var currentUser = buildCurrentUserProfile(1L);
        when(authenticatedContextFacade.getAuthenticatedUserProfile()).thenReturn(currentUser);

        when(notificationQueryService.handle(any(CountUnreadNotificationsByUserIdQuery.class)))
                .thenReturn(3L);

        mockMvc.perform(get("/api/v1/notifications/me/unread-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.unreadCount").value(3));
    }

    @Test
    @DisplayName("Debería marcar una notificación como leída")
    void shouldMarkNotificationAsReadSuccessfully() throws Exception {
        var currentUser = buildCurrentUserProfile(1L);
        when(authenticatedContextFacade.getAuthenticatedUserProfile()).thenReturn(currentUser);

        Notification existingNotification = buildNotification(1L, 1L, "Reserva confirmada", "Tu reserva fue creada", false);
        Notification updatedNotification = buildNotification(1L, 1L, "Reserva confirmada", "Tu reserva fue creada", true);

        when(notificationQueryService.handle(any(GetNotificationByIdQuery.class)))
                .thenReturn(Optional.of(existingNotification));

        when(notificationCommandService.handle(any(MarkNotificationAsReadCommand.class)))
                .thenReturn(Optional.of(updatedNotification));

        mockMvc.perform(post("/api/v1/notifications/1/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Debería eliminar una notificación correctamente")
    void shouldDeleteNotificationSuccessfully() throws Exception {
        var currentUser = buildCurrentUserProfile(1L);
        when(authenticatedContextFacade.getAuthenticatedUserProfile()).thenReturn(currentUser);

        Notification existingNotification = buildNotification(1L, 1L, "Reserva confirmada", "Tu reserva fue creada", false);

        when(notificationQueryService.handle(any(GetNotificationByIdQuery.class)))
                .thenReturn(Optional.of(existingNotification));

        doNothing().when(notificationCommandService).handle(any(DeleteNotificationCommand.class));

        mockMvc.perform(delete("/api/v1/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("\"Notification deleted successfully.\""));
    }

    @Test
    @DisplayName("No debería permitir crear notificaciones manualmente")
    void shouldRejectManualNotificationCreation() throws Exception {
        mockMvc.perform(post("/api/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("No debería permitir actualizar notificaciones manualmente")
    void shouldRejectManualNotificationUpdate() throws Exception {
        mockMvc.perform(put("/api/v1/notifications/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isMethodNotAllowed());
    }
}