package com.upc.courtly.notifications.interfaces.rest;

import com.upc.courtly.coaches.infrastructure.persistence.jpa.repositories.CoachRepository;
import com.upc.courtly.iam.interfaces.acl.AuthenticatedContextFacade;
import com.upc.courtly.notifications.domain.model.commands.DeleteNotificationCommand;
import com.upc.courtly.notifications.domain.model.commands.MarkNotificationAsReadCommand;
import com.upc.courtly.notifications.domain.model.queries.CountUnreadNotificationsByUserIdQuery;
import com.upc.courtly.notifications.domain.model.queries.GetAllNotificationsQuery;
import com.upc.courtly.notifications.domain.model.queries.GetNotificationByIdQuery;
import com.upc.courtly.notifications.domain.model.queries.GetNotificationsByUserIdQuery;
import com.upc.courtly.notifications.domain.services.NotificationCommandService;
import com.upc.courtly.notifications.domain.services.NotificationQueryService;
import com.upc.courtly.notifications.interfaces.rest.resources.NotificationCountResource;
import com.upc.courtly.notifications.interfaces.rest.resources.NotificationResource;
import com.upc.courtly.notifications.interfaces.rest.resources.UpdateNotificationResource;
import com.upc.courtly.notifications.interfaces.rest.transform.NotificationResourceFromEntityAssembler;
import com.upc.courtly.notifications.interfaces.rest.transform.UpdateNotificationCommandFromResourceAssembler;
import com.upc.courtly.users.infrastructure.persistence.jpa.repositories.UserProfileRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Notifications", description = "Notification Management Endpoints")
public class NotificationsController {
    private final NotificationCommandService notificationCommandService;
    private final NotificationQueryService notificationQueryService;
    private final AuthenticatedContextFacade authenticatedContextFacade;
    private final CoachRepository coachRepository;
    private final UserProfileRepository userProfileRepository;

    public NotificationsController(NotificationCommandService notificationCommandService,
                                   NotificationQueryService notificationQueryService,
                                   AuthenticatedContextFacade authenticatedContextFacade,
                                   CoachRepository coachRepository, UserProfileRepository userProfileRepository) {
        this.notificationCommandService = notificationCommandService;
        this.notificationQueryService = notificationQueryService;
        this.authenticatedContextFacade = authenticatedContextFacade;
        this.coachRepository = coachRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @PostMapping
    public ResponseEntity<NotificationResource> createNotification() {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED,
                "Notifications are created from business events and cannot be created manually");
    }

    @GetMapping
    public ResponseEntity<List<NotificationResource>> getAllNotifications() {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        var notifications = notificationQueryService.handle(new GetNotificationsByUserIdQuery(currentUserProfile.getId()));
        var notificationResources = notifications.stream()
                .map(NotificationResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(notificationResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResource> getNotificationById(@PathVariable Long id) {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        var query = new GetNotificationByIdQuery(id);
        var notification = notificationQueryService.handle(query);
        notification.ifPresent(value -> {
            if (!value.getUser().getId().equals(currentUserProfile.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own notifications");
            }
        });
        return notification.map(n -> ResponseEntity.ok(NotificationResourceFromEntityAssembler.toResourceFromEntity(n)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<NotificationResource>> getNotificationsByUserId(@PathVariable Long userId) {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        if (!currentUserProfile.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own notifications");
        }
        var notifications = notificationQueryService.handle(new GetNotificationsByUserIdQuery(userId)).stream()
                .map(NotificationResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/me")
    public ResponseEntity<List<NotificationResource>> getMyNotifications() {
        var user = authenticatedContextFacade.getAuthenticatedUser();
        Long profileId;

        var coachProfile = coachRepository.findByUserId(user.getId());
        if (coachProfile.isPresent()) {
            profileId = coachProfile.get().getId();
        } else {
            var userProfile = userProfileRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                            HttpStatus.NOT_FOUND, "No se encontró un perfil asociado a esta cuenta."));
            profileId = userProfile.getId();
        }

        var notifications = notificationQueryService.handle(new GetNotificationsByUserIdQuery(profileId))
                .stream()
                .map(NotificationResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/users/{userId}/unread-count")
    public ResponseEntity<NotificationCountResource> getUnreadCountByUserId(@PathVariable Long userId) {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        if (!currentUserProfile.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own notifications");
        }
        var unreadCount = notificationQueryService.handle(new CountUnreadNotificationsByUserIdQuery(userId));
        return ResponseEntity.ok(new NotificationCountResource(userId, unreadCount));
    }

    @GetMapping("/me/unread-count")
    public ResponseEntity<NotificationCountResource> getMyUnreadCount() {
        var user = authenticatedContextFacade.getAuthenticatedUser();
        Long profileId;

        var coachProfile = coachRepository.findByUserId(user.getId());
        if (coachProfile.isPresent()) {
            profileId = coachProfile.get().getId();
        } else {
            var userProfile = userProfileRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                            HttpStatus.NOT_FOUND, "No se encontró un perfil asociado."));
            profileId = userProfile.getId();
        }

        var unreadCount = notificationQueryService.handle(new CountUnreadNotificationsByUserIdQuery(profileId));
        return ResponseEntity.ok(new NotificationCountResource(profileId, unreadCount));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationResource> updateNotification(@PathVariable Long id, @RequestBody UpdateNotificationResource resource) {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED,
                "Notifications are created from business events and cannot be updated manually");
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<NotificationResource> markNotificationAsRead(@PathVariable Long id) {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        var existingNotification = notificationQueryService.handle(new GetNotificationByIdQuery(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        if (!existingNotification.getUser().getId().equals(currentUserProfile.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own notifications");
        }
        var updatedNotification = notificationCommandService.handle(new MarkNotificationAsReadCommand(id));
        return updatedNotification.map(value -> ResponseEntity.ok(NotificationResourceFromEntityAssembler.toResourceFromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        var existingNotification = notificationQueryService.handle(new GetNotificationByIdQuery(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        if (!existingNotification.getUser().getId().equals(currentUserProfile.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own notifications");
        }
        var command = new DeleteNotificationCommand(id);
        notificationCommandService.handle(command);
        return ResponseEntity.ok("Notification deleted successfully.");
    }
}
