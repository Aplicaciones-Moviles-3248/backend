package com.upc.courtly.matches.application.internal.commandservices;

import com.upc.courtly.courts.infrastructure.persistence.jpa.repositories.CourtRepository;
import com.upc.courtly.matches.domain.model.aggregates.Match;
import com.upc.courtly.matches.domain.model.commands.CreateMatchCommand;
import com.upc.courtly.matches.domain.model.commands.DeleteMatchCommand;
import com.upc.courtly.matches.domain.model.commands.UpdateMatchCommand;
import com.upc.courtly.matches.domain.services.MatchCommandService;
import com.upc.courtly.matches.infrastructure.persistence.jpa.repositories.MatchRepository;
import com.upc.courtly.users.infrastructure.persistence.jpa.repositories.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MatchCommandServiceImpl implements MatchCommandService {
    private final MatchRepository matchRepository;
    private final CourtRepository courtRepository;
    private final UserProfileRepository userProfileRepository;

    public MatchCommandServiceImpl(MatchRepository matchRepository, CourtRepository courtRepository, UserProfileRepository userProfileRepository) {
        this.matchRepository = matchRepository;
        this.courtRepository = courtRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public Optional<Match> handle(CreateMatchCommand command) {
        var court = courtRepository.findById(command.courtId()).orElseThrow(() -> new IllegalArgumentException("Court with id " + command.courtId() + " not found"));
        var createdBy = userProfileRepository.findById(command.createdById()).orElseThrow(() -> new IllegalArgumentException("User with id " + command.createdById() + " not found"));
        var match = new Match(command.title(), command.description(), command.dateTime(), command.status(), command.maxPlayers(), command.currentPlayers(), court, createdBy);
        var createdMatch = matchRepository.save(match);
        return Optional.of(createdMatch);
    }

    @Override
    public Optional<Match> handle(UpdateMatchCommand command) {
        return matchRepository.findById(command.matchId()).map(matchToUpdate -> {
            matchToUpdate.updateMatch(command.title(), command.description(), command.dateTime(), command.status(), command.maxPlayers(), command.currentPlayers());
            return matchRepository.save(matchToUpdate);
        });
    }

    @Override
    public void handle(DeleteMatchCommand command) {
        if (!matchRepository.existsById(command.matchId())) {
            throw new IllegalArgumentException("Match with id " + command.matchId() + " not found");
        }
        matchRepository.deleteById(command.matchId());
    }
}
