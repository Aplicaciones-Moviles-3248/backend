package com.upc.courtly.notifications.domain.services;

import com.upc.courtly.notifications.domain.model.aggregates.Notification;
import com.upc.courtly.notifications.domain.model.commands.CreateNotificationCommand;
import com.upc.courtly.notifications.domain.model.commands.DeleteNotificationCommand;
import com.upc.courtly.notifications.domain.model.commands.MarkNotificationAsReadCommand;
import com.upc.courtly.notifications.domain.model.commands.UpdateNotificationCommand;

import java.util.Optional;

public interface NotificationCommandService {
    Optional<Notification> handle(CreateNotificationCommand command);
    Optional<Notification> handle(UpdateNotificationCommand command);
    Optional<Notification> handle(MarkNotificationAsReadCommand command);
    void handle(DeleteNotificationCommand command);
}
