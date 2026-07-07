package com.upc.courtly.matches.interfaces.rest.resources;

import java.time.LocalDateTime;
import java.util.List;

public record MatchJoinRequestResource(Long id, Long matchId, UserSummaryResource requester, String status,
                                       List<UserSummaryResource> approvedBy, int requiredApprovals,
                                       LocalDateTime createdAt, LocalDateTime resolvedAt) {
    public record UserSummaryResource(Long id, String name) {}
}
