package com.upc.courtly.matches.domain.model.aggregates;

import com.upc.courtly.courts.domain.model.aggregates.Court;
import com.upc.courtly.matches.domain.model.valueobjects.MatchStatus;
import com.upc.courtly.users.domain.model.aggregates.UserProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
@Getter
@Setter
@NoArgsConstructor
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus status;

    @Column(nullable = false)
    private Integer maxPlayers;

    @Column(nullable = false)
    private Integer currentPlayers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private UserProfile createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Match(String title, String description, LocalDateTime dateTime, MatchStatus status, Integer maxPlayers, Integer currentPlayers, Court court, UserProfile createdBy) {
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.status = status;
        this.maxPlayers = maxPlayers;
        this.currentPlayers = currentPlayers;
        this.court = court;
        this.createdBy = createdBy;
    }

    public void updateMatch(String title, String description, LocalDateTime dateTime, MatchStatus status, Integer maxPlayers, Integer currentPlayers) {
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.status = status;
        this.maxPlayers = maxPlayers;
        this.currentPlayers = currentPlayers;
    }
}
