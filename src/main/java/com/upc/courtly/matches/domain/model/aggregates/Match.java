package com.upc.courtly.matches.domain.model.aggregates;

import com.upc.courtly.courts.domain.model.aggregates.Court;
import com.upc.courtly.matches.domain.model.valueobjects.MatchStatus;
import com.upc.courtly.users.domain.model.aggregates.UserProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

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

    @ManyToMany
    @JoinTable(name = "match_participants",
            joinColumns = @JoinColumn(name = "match_id"),
            inverseJoinColumns = @JoinColumn(name = "user_profile_id"))
    private Set<UserProfile> participants = new LinkedHashSet<>();

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
        this.participants.add(createdBy);
    }

    public void updateMatch(String title, String description, LocalDateTime dateTime, MatchStatus status, Integer maxPlayers, Integer currentPlayers) {
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.status = status;
        this.maxPlayers = maxPlayers;
        this.currentPlayers = currentPlayers;
    }

    public boolean hasParticipant(UserProfile userProfile) {
        return this.participants.stream().anyMatch(participant -> participant.getId().equals(userProfile.getId()));
    }

    public void join(UserProfile userProfile) {
        if (hasParticipant(userProfile)) {
            throw new IllegalArgumentException("User is already participating in this match");
        }
        if (this.status == MatchStatus.CANCELLED || this.status == MatchStatus.COMPLETED) {
            throw new IllegalArgumentException("This match is not open for new participants");
        }
        if (this.currentPlayers >= this.maxPlayers) {
            throw new IllegalArgumentException("This match has no available slots");
        }
        this.participants.add(userProfile);
        recomputeStatus();
    }

    public void recomputeStatus() {
        this.currentPlayers = this.participants.size();
        if (this.status != MatchStatus.CANCELLED && this.status != MatchStatus.COMPLETED) {
            this.status = this.currentPlayers >= this.maxPlayers ? MatchStatus.FULL : MatchStatus.OPEN;
        }
    }
}
