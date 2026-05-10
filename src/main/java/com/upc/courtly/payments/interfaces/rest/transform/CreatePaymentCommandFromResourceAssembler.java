package com.upc.courtly.payments.interfaces.rest.transform;

import com.upc.courtly.payments.domain.model.commands.CreatePaymentCommand;
import com.upc.courtly.payments.interfaces.rest.resources.CreatePaymentResource;

public class CreatePaymentCommandFromResourceAssembler {
    public static CreatePaymentCommand toCommandFromResource(CreatePaymentResource resource) {
        return new CreatePaymentCommand(
                resource.amount(),
                resource.userId()
        );
    }
}

