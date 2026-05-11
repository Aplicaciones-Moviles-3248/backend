package com.upc.courtly.coaches.interfaces.rest;

import com.upc.courtly.iam.interfaces.acl.AuthenticatedContextFacade;
import com.upc.courtly.coaches.domain.model.commands.DeleteCoachCommand;
import com.upc.courtly.coaches.domain.model.queries.GetAllCoachesQuery;
import com.upc.courtly.coaches.domain.model.queries.GetCoachByIdQuery;
import com.upc.courtly.coaches.domain.services.CoachCommandService;
import com.upc.courtly.coaches.domain.services.CoachQueryService;
import com.upc.courtly.coaches.interfaces.rest.resources.CoachResource;
import com.upc.courtly.coaches.interfaces.rest.resources.CreateCoachResource;
import com.upc.courtly.coaches.interfaces.rest.resources.UpdateCoachResource;
import com.upc.courtly.coaches.interfaces.rest.transform.CoachResourceFromEntityAssembler;
import com.upc.courtly.coaches.interfaces.rest.transform.CreateCoachCommandFromResourceAssembler;
import com.upc.courtly.coaches.interfaces.rest.transform.UpdateCoachCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/coaches", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Coaches", description = "Coach Management Endpoints")
public class CoachesController {
    private final CoachCommandService coachCommandService;
    private final CoachQueryService coachQueryService;
    private final AuthenticatedContextFacade authenticatedContextFacade;

    public CoachesController(CoachCommandService coachCommandService, CoachQueryService coachQueryService,
                             AuthenticatedContextFacade authenticatedContextFacade) {
        this.coachCommandService = coachCommandService;
        this.coachQueryService = coachQueryService;
        this.authenticatedContextFacade = authenticatedContextFacade;
    }

    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<CoachResource> createCoach(@RequestBody CreateCoachResource resource) {
        var currentUser = authenticatedContextFacade.getAuthenticatedUser();
        if (resource.userId() != null && !resource.userId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only create your own coach profile");
        }
        var command = CreateCoachCommandFromResourceAssembler.toCommandFromResource(resource);
        var coach = coachCommandService.handle(command);
        return coach.map(c -> new ResponseEntity<>(CoachResourceFromEntityAssembler.toResourceFromEntity(c), HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/me")
    public ResponseEntity<CoachResource> getMyCoachProfile() {
        var coach = authenticatedContextFacade.getAuthenticatedCoachProfile();
        return ResponseEntity.ok(CoachResourceFromEntityAssembler.toResourceFromEntity(coach));
    }

    @GetMapping
    public ResponseEntity<List<CoachResource>> getAllCoaches() {
        var query = new GetAllCoachesQuery();
        var coaches = coachQueryService.handle(query);
        var coachResources = coaches.stream()
                .map(CoachResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(coachResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoachResource> getCoachById(@PathVariable Long id) {
        var query = new GetCoachByIdQuery(id);
        var coach = coachQueryService.handle(query);
        return coach.map(c -> ResponseEntity.ok(CoachResourceFromEntityAssembler.toResourceFromEntity(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<CoachResource> updateCoach(@PathVariable Long id, @RequestBody UpdateCoachResource resource) {
        var currentCoach = authenticatedContextFacade.getAuthenticatedCoachProfile();
        if (!currentCoach.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own coach profile");
        }
        var command = UpdateCoachCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var updatedCoach = coachCommandService.handle(command);
        return updatedCoach.map(c -> ResponseEntity.ok(CoachResourceFromEntityAssembler.toResourceFromEntity(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<?> deleteCoach(@PathVariable Long id) {
        var currentCoach = authenticatedContextFacade.getAuthenticatedCoachProfile();
        if (!currentCoach.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own coach profile");
        }
        var command = new DeleteCoachCommand(id);
        coachCommandService.handle(command);
        return ResponseEntity.ok("Coach deleted successfully.");
    }
}
