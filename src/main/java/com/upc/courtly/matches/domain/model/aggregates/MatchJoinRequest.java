package com.upc.courtly.matches.domain.model.aggregates;

import com.upc.courtly.matches.domain.model.valueobjects.MatchJoinRequestStatus;
import com.upc.courtly.users.domain.model.aggregates.UserProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "match_join_requests")
@Getter
@Setter
@NoArgsConstructor
public class MatchJoinRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_user_id", nullable = false)
    private UserProfile requester;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchJoinRequestStatus status;

    @ManyToMany
    @JoinTable(name = "match_join_request_approvals",
            joinColumns = @JoinColumn(name = "join_request_id"),
            inverseJoinColumns = @JoinColumn(name = "user_profile_id"))
    private Set<UserProfile> approvedBy = new LinkedHashSet<>();

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime resolvedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public MatchJoinRequest(Match match, UserProfile requester) {
        this.match = match;
        this.requester = requester;
        this.status = MatchJoinRequestStatus.PENDING;
    }

    public boolean hasApproved(UserProfile approver) {
        return this.approvedBy.stream().anyMatch(u -> u.getId().equals(approver.getId()));
    }

    /**
     * Every current match participant must approve before the requester is let in.
     * The set of required approvers is captured at approval time (not creation time)
     * so late-joining participants before resolution are also asked to weigh in.
     */
    public boolean isFullyApproved() {
        return this.match.getParticipants().stream().allMatch(this::hasApproved);
    }

    public void approve(UserProfile approver) {
        if (this.status != MatchJoinRequestStatus.PENDING) {
            throw new IllegalStateException("This join request has already been resolved");
        }
        if (!this.match.hasParticipant(approver)) {
            throw new IllegalArgumentException("Only current match participants can approve join requests");
        }
        this.approvedBy.add(approver);
    }

    public void markApproved() {
        this.status = MatchJoinRequestStatus.APPROVED;
        this.resolvedAt = LocalDateTime.now();
    }

    public void reject() {
        this.status = MatchJoinRequestStatus.REJECTED;
        this.resolvedAt = LocalDateTime.now();
    }
}
