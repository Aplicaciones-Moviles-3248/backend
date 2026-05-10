package com.upc.courtly.coaches.domain.model.commands;

public record UpdateCoachCommand(Long coachId, String name, String expertise, String phone) {
}

