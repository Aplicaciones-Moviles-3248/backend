package com.upc.courtly.courts.interfaces.rest.resources;

import java.math.BigDecimal;

public record CourtResource(Long id, String name, String location, String type, String imageUrl, BigDecimal pricePerHour) {
}

