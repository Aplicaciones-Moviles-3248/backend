package com.upc.courtly.notifications.domain.services;

import com.upc.courtly.notifications.domain.model.aggregates.Notification;
import com.upc.courtly.notifications.domain.model.commands.CreateNotificationCommand;
import com.upc.courtly.notifications.domain.model.commands.DeleteNotificationCommand;
import com.upc.courtly.notifications.domain.model.commands.UpdateNotificationCommand;

import java.util.Optional;

public interface NotificationCommandService {
    Optional<Notification> handle(CreateNotificationCommand command);
    Optional<Notification> handle(UpdateNotificationCommand command);
    void handle(DeleteNotificationCommand command);
}
