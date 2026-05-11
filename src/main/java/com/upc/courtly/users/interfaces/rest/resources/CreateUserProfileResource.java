package com.upc.courtly.users.interfaces.rest.resources;

public record CreateUserProfileResource(String name, String email, String phone, String imageUrl, Long userId) {
}

