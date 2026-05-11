package com.upc.courtly.users.interfaces.rest.resources;

public record UpdateUserProfileResource(String name, String email, String phone, String imageUrl) {
}

