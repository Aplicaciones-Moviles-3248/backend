package com.upc.courtly.notifications.application.internal.commandservices;

import com.upc.courtly.notifications.domain.model.aggregates.Notification;
import com.upc.courtly.notifications.domain.model.commands.CreateNotificationCommand;
import com.upc.courtly.notifications.domain.model.commands.DeleteNotificationCommand;
import com.upc.courtly.notifications.domain.model.commands.UpdateNotificationCommand;
import com.upc.courtly.notifications.domain.services.NotificationCommandService;
import com.upc.courtly.notifications.infrastructure.persistence.jpa.repositories.NotificationRepository;
import com.upc.courtly.users.infrastructure.persistence.jpa.repositories.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotificationCommandServiceImpl implements NotificationCommandService {
    private final NotificationRepository notificationRepository;
    private final UserProfileRepository userProfileRepository;

    public NotificationCommandServiceImpl(NotificationRepository notificationRepository, UserProfileRepository userProfileRepository) {
        this.notificationRepository = notificationRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public Optional<Notification> handle(CreateNotificationCommand command) {
        var user = userProfileRepository.findById(command.userId()).orElseThrow(() -> new IllegalArgumentException("User with id " + command.userId() + " not found"));
        var notification = new Notification(command.title(), command.message(), command.type(), command.isRead(), user);
        var createdNotification = notificationRepository.save(notification);
        return Optional.of(createdNotification);
    }

    @Override
    public Optional<Notification> handle(UpdateNotificationCommand command) {
        return notificationRepository.findById(command.notificationId()).map(notificationToUpdate -> {
            notificationToUpdate.updateNotification(command.title(), command.message(), command.type(), command.isRead());
            return notificationRepository.save(notificationToUpdate);
        });
    }

    @Override
    public void handle(DeleteNotificationCommand command) {
        if (!notificationRepository.existsById(command.notificationId())) {
            throw new IllegalArgumentException("Notification with id " + command.notificationId() + " not found");
        }
        notificationRepository.deleteById(command.notificationId());
    }
}
