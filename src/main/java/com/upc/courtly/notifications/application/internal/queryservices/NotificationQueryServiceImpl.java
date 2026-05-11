package com.upc.courtly.notifications.application.internal.queryservices;

import com.upc.courtly.notifications.domain.model.aggregates.Notification;
import com.upc.courtly.notifications.domain.model.queries.CountUnreadNotificationsByUserIdQuery;
import com.upc.courtly.notifications.domain.model.queries.GetAllNotificationsQuery;
import com.upc.courtly.notifications.domain.model.queries.GetNotificationByIdQuery;
import com.upc.courtly.notifications.domain.model.queries.GetNotificationsByUserIdQuery;
import com.upc.courtly.notifications.domain.services.NotificationQueryService;
import com.upc.courtly.notifications.infrastructure.persistence.jpa.repositories.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationQueryServiceImpl implements NotificationQueryService {
    private final NotificationRepository notificationRepository;

    public NotificationQueryServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public List<Notification> handle(GetAllNotificationsQuery query) {
        return notificationRepository.findAll();
    }

    @Override
    public Optional<Notification> handle(GetNotificationByIdQuery query) {
        return notificationRepository.findById(query.notificationId());
    }

    @Override
    public List<Notification> handle(GetNotificationsByUserIdQuery query) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(query.userId());
    }

    @Override
    public long handle(CountUnreadNotificationsByUserIdQuery query) {
        return notificationRepository.countByUserIdAndIsReadFalse(query.userId());
    }
}
