package com.upc.courtly.courts.domain.model.commands;

import java.math.BigDecimal;

public record UpdateCourtCommand(Long courtId, String name, String location, String type, String imageUrl, BigDecimal pricePerHour) {
}

