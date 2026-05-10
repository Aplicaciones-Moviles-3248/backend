package com.upc.courtly.matches.domain.model.commands;

import com.upc.courtly.matches.domain.model.valueobjects.MatchStatus;

import java.time.LocalDateTime;

public record CreateMatchCommand(String title, String description, LocalDateTime dateTime, MatchStatus status, Integer maxPlayers, Integer currentPlayers, Long courtId, Long createdById) {
}
