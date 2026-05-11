package com.upc.courtly.notifications.domain.model.aggregates;

import com.upc.courtly.notifications.domain.model.valueobjects.NotificationType;
import com.upc.courtly.users.domain.model.aggregates.UserProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private boolean isRead;

    @Column
    private String relatedEntityType;

    @Column
    private Long relatedEntityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile user;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Notification(String title, String message, NotificationType type, boolean isRead,
                        String relatedEntityType, Long relatedEntityId, UserProfile user) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.relatedEntityType = relatedEntityType;
        this.relatedEntityId = relatedEntityId;
        this.user = user;
    }

    public void updateNotification(String title, String message, NotificationType type, boolean isRead,
                                   String relatedEntityType, Long relatedEntityId) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.relatedEntityType = relatedEntityType;
        this.relatedEntityId = relatedEntityId;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
