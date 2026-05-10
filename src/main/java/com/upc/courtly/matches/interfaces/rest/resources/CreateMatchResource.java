package com.upc.courtly.matches.interfaces.rest.resources;

import java.time.LocalDateTime;

public record CreateMatchResource(String title, String description, LocalDateTime dateTime, String status, Integer maxPlayers, Integer currentPlayers, Long courtId, Long createdById) {
}
