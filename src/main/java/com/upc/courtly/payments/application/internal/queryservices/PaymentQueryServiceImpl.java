package com.upc.courtly.payments.application.internal.queryservices;

import com.upc.courtly.payments.domain.model.aggregates.Payment;
import com.upc.courtly.payments.domain.model.queries.GetPaymentByIdQuery;
import com.upc.courtly.payments.domain.services.PaymentQueryService;
import com.upc.courtly.payments.infrastructure.persistence.jpa.repositories.PaymentRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class PaymentQueryServiceImpl implements PaymentQueryService {
    private final PaymentRepository paymentRepository;

    public PaymentQueryServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Optional<Payment> handle(GetPaymentByIdQuery query) {
        return paymentRepository.findById(query.paymentId());
    }
}

