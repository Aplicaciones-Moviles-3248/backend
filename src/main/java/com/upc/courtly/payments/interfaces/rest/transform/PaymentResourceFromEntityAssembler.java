package com.upc.courtly.payments.interfaces.rest.transform;

import com.upc.courtly.payments.domain.model.aggregates.Payment;
import com.upc.courtly.payments.interfaces.rest.resources.PaymentResource;

public class PaymentResourceFromEntityAssembler {
    public static PaymentResource toResourceFromEntity(Payment entity) {
        var userSummary = new PaymentResource.UserSummaryResource(
                entity.getUser().getId(),
                entity.getUser().getName()
        );
        return new PaymentResource(
                entity.getId(),
                entity.getAmount(),
                entity.getPaymentDate(),
                entity.getStatus(),
                entity.getContextType(),
                entity.getBooking() != null ? entity.getBooking().getId() : null,
                entity.getTrainingSession() != null ? entity.getTrainingSession().getId() : null,
                userSummary
        );
    }
}

