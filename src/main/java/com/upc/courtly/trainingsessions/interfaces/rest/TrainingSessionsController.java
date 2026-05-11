package com.upc.courtly.trainingsessions.interfaces.rest;

import com.upc.courtly.iam.interfaces.acl.AuthenticatedContextFacade;
import com.upc.courtly.trainingsessions.domain.model.commands.*;
import com.upc.courtly.trainingsessions.domain.model.queries.GetAllTrainingSessionsQuery;
import com.upc.courtly.trainingsessions.domain.model.queries.GetTrainingSessionByIdQuery;
import com.upc.courtly.trainingsessions.domain.services.TrainingSessionCommandService;
import com.upc.courtly.trainingsessions.domain.services.TrainingSessionQueryService;
import com.upc.courtly.trainingsessions.interfaces.rest.resources.CreateTrainingSessionResource;
import com.upc.courtly.trainingsessions.interfaces.rest.resources.TrainingSessionActionResource;
import com.upc.courtly.trainingsessions.interfaces.rest.resources.TrainingSessionResource;
import com.upc.courtly.trainingsessions.interfaces.rest.transform.CreateTrainingSessionCommandFromResourceAssembler;
import com.upc.courtly.trainingsessions.interfaces.rest.transform.TrainingSessionResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/training-sessions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Training Sessions", description = "Training Session Management Endpoints")
public class TrainingSessionsController {
    private final TrainingSessionCommandService trainingSessionCommandService;
    private final TrainingSessionQueryService trainingSessionQueryService;
    private final AuthenticatedContextFacade authenticatedContextFacade;

    public TrainingSessionsController(TrainingSessionCommandService trainingSessionCommandService,
                                      TrainingSessionQueryService trainingSessionQueryService,
                                      AuthenticatedContextFacade authenticatedContextFacade) {
        this.trainingSessionCommandService = trainingSessionCommandService;
        this.trainingSessionQueryService = trainingSessionQueryService;
        this.authenticatedContextFacade = authenticatedContextFacade;
    }

    @PostMapping
    public ResponseEntity<TrainingSessionResource> createTrainingSession(@RequestBody CreateTrainingSessionResource resource) {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        if (!currentUserProfile.getId().equals(resource.playerId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only create training sessions for your own profile");
        }
        var command = CreateTrainingSessionCommandFromResourceAssembler.toCommandFromResource(resource);
        var trainingSession = trainingSessionCommandService.handle(command);
        return trainingSession.map(session -> new ResponseEntity<>(TrainingSessionResourceFromEntityAssembler.toResourceFromEntity(session), HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping
    public ResponseEntity<List<TrainingSessionResource>> getAllTrainingSessions() {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        var currentCoach = safeCurrentCoach();
        var sessions = trainingSessionQueryService.handle(new GetAllTrainingSessionsQuery()).stream()
                .filter(session -> session.getPlayer().getId().equals(currentUserProfile.getId())
                        || (currentCoach != null && session.getCoach().getId().equals(currentCoach.getId())))
                .map(TrainingSessionResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingSessionResource> getTrainingSessionById(@PathVariable Long id) {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        var currentCoach = safeCurrentCoach();
        var session = trainingSessionQueryService.handle(new GetTrainingSessionByIdQuery(id));
        session.ifPresent(value -> {
            boolean owner = value.getPlayer().getId().equals(currentUserProfile.getId())
                    || (currentCoach != null && value.getCoach().getId().equals(currentCoach.getId()));
            if (!owner) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own training sessions");
            }
        });
        return session.map(value -> ResponseEntity.ok(TrainingSessionResourceFromEntityAssembler.toResourceFromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<TrainingSessionResource> acceptTrainingSession(@PathVariable Long id) {
        var currentCoach = authenticatedContextFacade.getAuthenticatedCoachProfile();
        var existingSession = trainingSessionQueryService.handle(new GetTrainingSessionByIdQuery(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Training session not found"));
        if (!existingSession.getCoach().getId().equals(currentCoach.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only accept your own training session requests");
        }
        var session = trainingSessionCommandService.handle(new AcceptTrainingSessionCommand(id));
        return session.map(value -> ResponseEntity.ok(TrainingSessionResourceFromEntityAssembler.toResourceFromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<TrainingSessionResource> rejectTrainingSession(@PathVariable Long id, @RequestBody(required = false) TrainingSessionActionResource resource) {
        var currentCoach = authenticatedContextFacade.getAuthenticatedCoachProfile();
        var existingSession = trainingSessionQueryService.handle(new GetTrainingSessionByIdQuery(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Training session not found"));
        if (!existingSession.getCoach().getId().equals(currentCoach.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only reject your own training session requests");
        }
        var session = trainingSessionCommandService.handle(new RejectTrainingSessionCommand(id, resource != null ? resource.reason() : null));
        return session.map(value -> ResponseEntity.ok(TrainingSessionResourceFromEntityAssembler.toResourceFromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<TrainingSessionResource> cancelTrainingSession(@PathVariable Long id, @RequestBody(required = false) TrainingSessionActionResource resource) {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        var currentCoach = safeCurrentCoach();
        var existingSession = trainingSessionQueryService.handle(new GetTrainingSessionByIdQuery(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Training session not found"));
        boolean owner = existingSession.getPlayer().getId().equals(currentUserProfile.getId())
                || (currentCoach != null && existingSession.getCoach().getId().equals(currentCoach.getId()));
        if (!owner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only cancel your own training sessions");
        }
        var session = trainingSessionCommandService.handle(new CancelTrainingSessionCommand(id, resource != null ? resource.reason() : null));
        return session.map(value -> ResponseEntity.ok(TrainingSessionResourceFromEntityAssembler.toResourceFromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<TrainingSessionResource> completeTrainingSession(@PathVariable Long id) {
        var currentCoach = authenticatedContextFacade.getAuthenticatedCoachProfile();
        var existingSession = trainingSessionQueryService.handle(new GetTrainingSessionByIdQuery(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Training session not found"));
        if (!existingSession.getCoach().getId().equals(currentCoach.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only complete your own training sessions");
        }
        var session = trainingSessionCommandService.handle(new CompleteTrainingSessionCommand(id));
        return session.map(value -> ResponseEntity.ok(TrainingSessionResourceFromEntityAssembler.toResourceFromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrainingSession(@PathVariable Long id) {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        var currentCoach = safeCurrentCoach();
        var existingSession = trainingSessionQueryService.handle(new GetTrainingSessionByIdQuery(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Training session not found"));
        boolean owner = existingSession.getPlayer().getId().equals(currentUserProfile.getId())
                || (currentCoach != null && existingSession.getCoach().getId().equals(currentCoach.getId()));
        if (!owner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own training sessions");
        }
        trainingSessionCommandService.handle(new DeleteTrainingSessionCommand(id));
        return ResponseEntity.ok("Training session deleted successfully.");
    }

    private com.upc.courtly.coaches.domain.model.aggregates.Coach safeCurrentCoach() {
        try {
            return authenticatedContextFacade.getAuthenticatedCoachProfile();
        } catch (IllegalStateException exception) {
            return null;
        }
    }
}
