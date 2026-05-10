package com.upc.courtly.notifications.infrastructure.persistence.jpa.repositories;

import com.upc.courtly.notifications.domain.model.aggregates.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
