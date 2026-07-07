package com.upc.courtly.matches.interfaces.rest;

import com.upc.courtly.iam.interfaces.acl.AuthenticatedContextFacade;
import com.upc.courtly.matches.domain.model.commands.ApproveMatchJoinRequestCommand;
import com.upc.courtly.matches.domain.model.commands.CreateMatchJoinRequestCommand;
import com.upc.courtly.matches.domain.model.queries.GetJoinRequestByIdQuery;
import com.upc.courtly.matches.domain.model.queries.GetJoinRequestsByMatchIdQuery;
import com.upc.courtly.matches.domain.services.MatchJoinRequestCommandService;
import com.upc.courtly.matches.domain.services.MatchJoinRequestQueryService;
import com.upc.courtly.matches.interfaces.rest.resources.MatchJoinRequestResource;
import com.upc.courtly.matches.interfaces.rest.transform.MatchJoinRequestResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/matches/{matchId}/join-requests", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Match Join Requests", description = "Consensus-based Match Join Request Endpoints")
public class MatchJoinRequestsController {
    private final MatchJoinRequestCommandService matchJoinRequestCommandService;
    private final MatchJoinRequestQueryService matchJoinRequestQueryService;
    private final AuthenticatedContextFacade authenticatedContextFacade;

    public MatchJoinRequestsController(MatchJoinRequestCommandService matchJoinRequestCommandService,
                                       MatchJoinRequestQueryService matchJoinRequestQueryService,
                                       AuthenticatedContextFacade authenticatedContextFacade) {
        this.matchJoinRequestCommandService = matchJoinRequestCommandService;
        this.matchJoinRequestQueryService = matchJoinRequestQueryService;
        this.authenticatedContextFacade = authenticatedContextFacade;
    }

    @PostMapping
    public ResponseEntity<MatchJoinRequestResource> createJoinRequest(@PathVariable Long matchId) {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        var command = new CreateMatchJoinRequestCommand(matchId, currentUserProfile.getId());
        var joinRequest = matchJoinRequestCommandService.handle(command);
        return joinRequest.map(jr -> new ResponseEntity<>(MatchJoinRequestResourceFromEntityAssembler.toResourceFromEntity(jr), HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping
    public ResponseEntity<List<MatchJoinRequestResource>> getJoinRequestsForMatch(@PathVariable Long matchId) {
        var joinRequests = matchJoinRequestQueryService.handle(new GetJoinRequestsByMatchIdQuery(matchId)).stream()
                .map(MatchJoinRequestResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(joinRequests);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<MatchJoinRequestResource> getJoinRequestById(@PathVariable Long matchId, @PathVariable Long requestId) {
        var joinRequest = matchJoinRequestQueryService.handle(new GetJoinRequestByIdQuery(requestId))
                .filter(jr -> jr.getMatch().getId().equals(matchId));
        return joinRequest.map(jr -> ResponseEntity.ok(MatchJoinRequestResourceFromEntityAssembler.toResourceFromEntity(jr)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{requestId}/approve")
    public ResponseEntity<MatchJoinRequestResource> approveJoinRequest(@PathVariable Long matchId, @PathVariable Long requestId) {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        var existingRequest = matchJoinRequestQueryService.handle(new GetJoinRequestByIdQuery(requestId))
                .filter(jr -> jr.getMatch().getId().equals(matchId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Join request not found"));
        if (!existingRequest.getMatch().hasParticipant(currentUserProfile)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only current match participants can approve join requests");
        }
        var command = new ApproveMatchJoinRequestCommand(requestId, currentUserProfile.getId());
        var joinRequest = matchJoinRequestCommandService.handle(command);
        return joinRequest.map(jr -> ResponseEntity.ok(MatchJoinRequestResourceFromEntityAssembler.toResourceFromEntity(jr)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
