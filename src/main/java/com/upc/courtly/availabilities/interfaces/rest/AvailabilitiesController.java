package com.upc.courtly.availabilities.interfaces.rest;

import com.upc.courtly.availabilities.domain.model.commands.DeleteAvailabilityCommand;
import com.upc.courtly.availabilities.domain.model.queries.GetAllAvailabilitiesQuery;
import com.upc.courtly.availabilities.domain.model.queries.GetAvailabilityByIdQuery;
import com.upc.courtly.availabilities.domain.services.AvailabilityCommandService;
import com.upc.courtly.availabilities.domain.services.AvailabilityQueryService;
import com.upc.courtly.availabilities.interfaces.rest.resources.AvailabilityResource;
import com.upc.courtly.availabilities.interfaces.rest.resources.CreateAvailabilityResource;
import com.upc.courtly.availabilities.interfaces.rest.resources.UpdateAvailabilityResource;
import com.upc.courtly.availabilities.interfaces.rest.transform.AvailabilityResourceFromEntityAssembler;
import com.upc.courtly.availabilities.interfaces.rest.transform.CreateAvailabilityCommandFromResourceAssembler;
import com.upc.courtly.availabilities.interfaces.rest.transform.UpdateAvailabilityCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/availabilities", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Availabilities", description = "Availability Management Endpoints")
public class AvailabilitiesController {
    private final AvailabilityCommandService availabilityCommandService;
    private final AvailabilityQueryService availabilityQueryService;

    public AvailabilitiesController(AvailabilityCommandService availabilityCommandService, AvailabilityQueryService availabilityQueryService) {
        this.availabilityCommandService = availabilityCommandService;
        this.availabilityQueryService = availabilityQueryService;
    }

    @PostMapping
    public ResponseEntity<AvailabilityResource> createAvailability(@RequestBody CreateAvailabilityResource resource) {
        var command = CreateAvailabilityCommandFromResourceAssembler.toCommandFromResource(resource);
        var availability = availabilityCommandService.handle(command);
        return availability.map(a -> new ResponseEntity<>(AvailabilityResourceFromEntityAssembler.toResourceFromEntity(a), HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping
    public ResponseEntity<List<AvailabilityResource>> getAllAvailabilities() {
        var query = new GetAllAvailabilitiesQuery();
        var availabilities = availabilityQueryService.handle(query);
        var availabilityResources = availabilities.stream()
                .map(AvailabilityResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(availabilityResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvailabilityResource> getAvailabilityById(@PathVariable Long id) {
        var query = new GetAvailabilityByIdQuery(id);
        var availability = availabilityQueryService.handle(query);
        return availability.map(a -> ResponseEntity.ok(AvailabilityResourceFromEntityAssembler.toResourceFromEntity(a)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvailabilityResource> updateAvailability(@PathVariable Long id, @RequestBody UpdateAvailabilityResource resource) {
        var command = UpdateAvailabilityCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var updatedAvailability = availabilityCommandService.handle(command);
        return updatedAvailability.map(a -> ResponseEntity.ok(AvailabilityResourceFromEntityAssembler.toResourceFromEntity(a)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAvailability(@PathVariable Long id) {
        var command = new DeleteAvailabilityCommand(id);
        availabilityCommandService.handle(command);
        return ResponseEntity.ok("Availability deleted successfully.");
    }
}
