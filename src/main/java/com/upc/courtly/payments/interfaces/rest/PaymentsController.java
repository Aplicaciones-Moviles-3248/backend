package com.upc.courtly.payments.interfaces.rest;

import com.upc.courtly.iam.interfaces.acl.AuthenticatedContextFacade;
import com.upc.courtly.payments.domain.model.queries.GetPaymentByIdQuery;
import com.upc.courtly.payments.domain.services.PaymentCommandService;
import com.upc.courtly.payments.domain.services.PaymentQueryService;
import com.upc.courtly.payments.interfaces.rest.resources.CreatePaymentResource;
import com.upc.courtly.payments.interfaces.rest.resources.PaymentResource;
import com.upc.courtly.payments.interfaces.rest.transform.CreatePaymentCommandFromResourceAssembler;
import com.upc.courtly.payments.interfaces.rest.transform.PaymentResourceFromEntityAssembler;
import com.upc.courtly.payments.infrastructure.persistence.jpa.repositories.PaymentRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/payments", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Payments", description = "Payment Management Endpoints")
public class PaymentsController {
    private final PaymentCommandService paymentCommandService;
    private final PaymentQueryService paymentQueryService;
    private final PaymentRepository paymentRepository;
    private final AuthenticatedContextFacade authenticatedContextFacade;

    public PaymentsController(PaymentCommandService paymentCommandService, PaymentQueryService paymentQueryService,
                              PaymentRepository paymentRepository, AuthenticatedContextFacade authenticatedContextFacade) {
        this.paymentCommandService = paymentCommandService;
        this.paymentQueryService = paymentQueryService;
        this.paymentRepository = paymentRepository;
        this.authenticatedContextFacade = authenticatedContextFacade;
    }

    @PostMapping
    public ResponseEntity<PaymentResource> createPayment(@RequestBody CreatePaymentResource resource) {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        if (!currentUserProfile.getId().equals(resource.userId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only create payments for your own profile");
        }
        var command = CreatePaymentCommandFromResourceAssembler.toCommandFromResource(resource);
        var payment = paymentCommandService.handle(command);
        return payment.map(p -> new ResponseEntity<>(PaymentResourceFromEntityAssembler.toResourceFromEntity(p), HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping
    public ResponseEntity<List<PaymentResource>> getMyPayments() {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        var payments = paymentRepository.findByUserIdOrderByCreatedAtDesc(currentUserProfile.getId()).stream()
                .map(PaymentResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResource> getPaymentById(@PathVariable Long id) {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        var query = new GetPaymentByIdQuery(id);
        var payment = paymentQueryService.handle(query);
        payment.ifPresent(value -> {
            if (!value.getUser().getId().equals(currentUserProfile.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own payments");
            }
        });
        return payment.map(p -> ResponseEntity.ok(PaymentResourceFromEntityAssembler.toResourceFromEntity(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
