package com.upc.courtly.payments.domain.services;

import com.upc.courtly.payments.domain.model.aggregates.Payment;
import com.upc.courtly.payments.domain.model.commands.CreatePaymentCommand;
import java.util.Optional;

public interface PaymentCommandService {
    Optional<Payment> handle(CreatePaymentCommand command);
}

