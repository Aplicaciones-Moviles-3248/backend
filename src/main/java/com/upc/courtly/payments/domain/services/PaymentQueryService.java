package com.upc.courtly.payments.domain.services;

import com.upc.courtly.payments.domain.model.aggregates.Payment;
import com.upc.courtly.payments.domain.model.queries.GetPaymentByIdQuery;
import java.util.Optional;

public interface PaymentQueryService {
    Optional<Payment> handle(GetPaymentByIdQuery query);
}

