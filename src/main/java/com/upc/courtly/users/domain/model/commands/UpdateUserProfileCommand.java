package com.upc.courtly.users.domain.model.commands;

public record UpdateUserProfileCommand(Long userId, String name, String email, String phone) {
}

