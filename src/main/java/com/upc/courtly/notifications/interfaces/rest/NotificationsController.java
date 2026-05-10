package com.upc.courtly.notifications.interfaces.rest;

import com.upc.courtly.notifications.domain.model.commands.DeleteNotificationCommand;
import com.upc.courtly.notifications.domain.model.queries.GetAllNotificationsQuery;
import com.upc.courtly.notifications.domain.model.queries.GetNotificationByIdQuery;
import com.upc.courtly.notifications.domain.services.NotificationCommandService;
import com.upc.courtly.notifications.domain.services.NotificationQueryService;
import com.upc.courtly.notifications.interfaces.rest.resources.CreateNotificationResource;
import com.upc.courtly.notifications.interfaces.rest.resources.NotificationResource;
import com.upc.courtly.notifications.interfaces.rest.resources.UpdateNotificationResource;
import com.upc.courtly.notifications.interfaces.rest.transform.CreateNotificationCommandFromResourceAssembler;
import com.upc.courtly.notifications.interfaces.rest.transform.NotificationResourceFromEntityAssembler;
import com.upc.courtly.notifications.interfaces.rest.transform.UpdateNotificationCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Notifications", description = "Notification Management Endpoints")
public class NotificationsController {
    private final NotificationCommandService notificationCommandService;
    private final NotificationQueryService notificationQueryService;

    public NotificationsController(NotificationCommandService notificationCommandService, NotificationQueryService notificationQueryService) {
        this.notificationCommandService = notificationCommandService;
        this.notificationQueryService = notificationQueryService;
    }

    @PostMapping
    public ResponseEntity<NotificationResource> createNotification(@RequestBody CreateNotificationResource resource) {
        var command = CreateNotificationCommandFromResourceAssembler.toCommandFromResource(resource);
        var notification = notificationCommandService.handle(command);
        return notification.map(n -> new ResponseEntity<>(NotificationResourceFromEntityAssembler.toResourceFromEntity(n), HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping
    public ResponseEntity<List<NotificationResource>> getAllNotifications() {
        var query = new GetAllNotificationsQuery();
        var notifications = notificationQueryService.handle(query);
        var notificationResources = notifications.stream()
                .map(NotificationResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(notificationResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResource> getNotificationById(@PathVariable Long id) {
        var query = new GetNotificationByIdQuery(id);
        var notification = notificationQueryService.handle(query);
        return notification.map(n -> ResponseEntity.ok(NotificationResourceFromEntityAssembler.toResourceFromEntity(n)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationResource> updateNotification(@PathVariable Long id, @RequestBody UpdateNotificationResource resource) {
        var command = UpdateNotificationCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var updatedNotification = notificationCommandService.handle(command);
        return updatedNotification.map(n -> ResponseEntity.ok(NotificationResourceFromEntityAssembler.toResourceFromEntity(n)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        var command = new DeleteNotificationCommand(id);
        notificationCommandService.handle(command);
        return ResponseEntity.ok("Notification deleted successfully.");
    }
}
