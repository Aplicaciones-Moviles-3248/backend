package com.upc.courtly.matches.interfaces.rest.resources;

import java.time.LocalDateTime;
import java.util.List;

public record MatchResource(Long id, String title, String description, LocalDateTime dateTime, String status, Integer maxPlayers, Integer currentPlayers, LocalDateTime createdAt, CourtSummaryResource court, UserSummaryResource createdBy, List<UserSummaryResource> participants) {
    public record CourtSummaryResource(Long id, String name) {}
    public record UserSummaryResource(Long id, String name) {}
}
