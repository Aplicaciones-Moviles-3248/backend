package com.upc.courtly.payments.interfaces.rest.resources;

import java.math.BigDecimal;

public record CreatePaymentResource(BigDecimal amount, Long userId) {
}

