package com.upc.courtly.courts.interfaces.rest;

import com.upc.courtly.courts.domain.model.commands.DeleteCourtCommand;
import com.upc.courtly.courts.domain.model.queries.GetAllCourtsQuery;
import com.upc.courtly.courts.domain.model.queries.GetCourtByIdQuery;
import com.upc.courtly.courts.domain.services.CourtCommandService;
import com.upc.courtly.courts.domain.services.CourtQueryService;
import com.upc.courtly.courts.interfaces.rest.resources.CourtResource;
import com.upc.courtly.courts.interfaces.rest.resources.CreateCourtResource;
import com.upc.courtly.courts.interfaces.rest.resources.UpdateCourtResource;
import com.upc.courtly.courts.interfaces.rest.transform.CourtResourceFromEntityAssembler;
import com.upc.courtly.courts.interfaces.rest.transform.CreateCourtCommandFromResourceAssembler;
import com.upc.courtly.courts.interfaces.rest.transform.UpdateCourtCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/courts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Courts", description = "Court Management Endpoints")
public class CourtsController {
    private final CourtCommandService courtCommandService;
    private final CourtQueryService courtQueryService;

    public CourtsController(CourtCommandService courtCommandService, CourtQueryService courtQueryService) {
        this.courtCommandService = courtCommandService;
        this.courtQueryService = courtQueryService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourtResource> createCourt(@RequestBody CreateCourtResource resource) {
        var command = CreateCourtCommandFromResourceAssembler.toCommandFromResource(resource);
        var court = courtCommandService.handle(command);
        return court.map(c -> new ResponseEntity<>(CourtResourceFromEntityAssembler.toResourceFromEntity(c), HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping
    public ResponseEntity<List<CourtResource>> getAllCourts() {
        var query = new GetAllCourtsQuery();
        var courts = courtQueryService.handle(query);
        var courtResources = courts.stream()
                .map(CourtResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(courtResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourtResource> getCourtById(@PathVariable Long id) {
        var query = new GetCourtByIdQuery(id);
        var court = courtQueryService.handle(query);
        return court.map(c -> ResponseEntity.ok(CourtResourceFromEntityAssembler.toResourceFromEntity(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourtResource> updateCourt(@PathVariable Long id, @RequestBody UpdateCourtResource resource) {
        var command = UpdateCourtCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var updatedCourt = courtCommandService.handle(command);
        return updatedCourt.map(c -> ResponseEntity.ok(CourtResourceFromEntityAssembler.toResourceFromEntity(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCourt(@PathVariable Long id) {
        var command = new DeleteCourtCommand(id);
        courtCommandService.handle(command);
        return ResponseEntity.ok("Court deleted successfully.");
    }
}

