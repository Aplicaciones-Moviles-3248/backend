package com.upc.courtly.users.domain.model.commands;

public record CreateUserProfileCommand(String name, String email, String phone, String imageUrl, Long userId) {
}

