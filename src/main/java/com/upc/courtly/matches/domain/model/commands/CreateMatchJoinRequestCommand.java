package com.upc.courtly.matches.domain.model.commands;

public record CreateMatchJoinRequestCommand(Long matchId, Long requesterId) {
}
