package com.upc.courtly.matches.interfaces.rest;

import com.upc.courtly.matches.domain.model.commands.DeleteMatchCommand;
import com.upc.courtly.matches.domain.model.queries.GetAllMatchesQuery;
import com.upc.courtly.matches.domain.model.queries.GetMatchByIdQuery;
import com.upc.courtly.matches.domain.services.MatchCommandService;
import com.upc.courtly.matches.domain.services.MatchQueryService;
import com.upc.courtly.matches.interfaces.rest.resources.CreateMatchResource;
import com.upc.courtly.matches.interfaces.rest.resources.MatchResource;
import com.upc.courtly.matches.interfaces.rest.resources.UpdateMatchResource;
import com.upc.courtly.matches.interfaces.rest.transform.CreateMatchCommandFromResourceAssembler;
import com.upc.courtly.matches.interfaces.rest.transform.MatchResourceFromEntityAssembler;
import com.upc.courtly.matches.interfaces.rest.transform.UpdateMatchCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/matches", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Matches", description = "Match Management Endpoints")
public class MatchesController {
    private final MatchCommandService matchCommandService;
    private final MatchQueryService matchQueryService;

    public MatchesController(MatchCommandService matchCommandService, MatchQueryService matchQueryService) {
        this.matchCommandService = matchCommandService;
        this.matchQueryService = matchQueryService;
    }

    @PostMapping
    public ResponseEntity<MatchResource> createMatch(@RequestBody CreateMatchResource resource) {
        var command = CreateMatchCommandFromResourceAssembler.toCommandFromResource(resource);
        var match = matchCommandService.handle(command);
        return match.map(m -> new ResponseEntity<>(MatchResourceFromEntityAssembler.toResourceFromEntity(m), HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping
    public ResponseEntity<List<MatchResource>> getAllMatches() {
        var query = new GetAllMatchesQuery();
        var matches = matchQueryService.handle(query);
        var matchResources = matches.stream()
                .map(MatchResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(matchResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchResource> getMatchById(@PathVariable Long id) {
        var query = new GetMatchByIdQuery(id);
        var match = matchQueryService.handle(query);
        return match.map(m -> ResponseEntity.ok(MatchResourceFromEntityAssembler.toResourceFromEntity(m)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MatchResource> updateMatch(@PathVariable Long id, @RequestBody UpdateMatchResource resource) {
        var command = UpdateMatchCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var updatedMatch = matchCommandService.handle(command);
        return updatedMatch.map(m -> ResponseEntity.ok(MatchResourceFromEntityAssembler.toResourceFromEntity(m)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMatch(@PathVariable Long id) {
        var command = new DeleteMatchCommand(id);
        matchCommandService.handle(command);
        return ResponseEntity.ok("Match deleted successfully.");
    }
}
