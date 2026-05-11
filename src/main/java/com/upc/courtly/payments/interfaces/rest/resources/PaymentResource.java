package com.upc.courtly.payments.interfaces.rest.resources;

import com.upc.courtly.payments.domain.model.valueobjects.PaymentContextType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResource(Long id, BigDecimal amount, LocalDateTime paymentDate, String status,
                              PaymentContextType contextType, Long bookingId, Long trainingSessionId,
                              UserSummaryResource user) {
    public record UserSummaryResource(Long id, String name) {}
}

