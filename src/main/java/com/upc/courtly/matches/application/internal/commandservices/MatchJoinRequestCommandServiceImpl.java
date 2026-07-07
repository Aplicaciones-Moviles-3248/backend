package com.upc.courtly.matches.application.internal.commandservices;

import com.upc.courtly.matches.domain.model.aggregates.MatchJoinRequest;
import com.upc.courtly.matches.domain.model.commands.ApproveMatchJoinRequestCommand;
import com.upc.courtly.matches.domain.model.commands.CreateMatchJoinRequestCommand;
import com.upc.courtly.matches.domain.model.valueobjects.MatchJoinRequestStatus;
import com.upc.courtly.matches.domain.model.valueobjects.MatchStatus;
import com.upc.courtly.matches.domain.services.MatchJoinRequestCommandService;
import com.upc.courtly.matches.infrastructure.persistence.jpa.repositories.MatchJoinRequestRepository;
import com.upc.courtly.matches.infrastructure.persistence.jpa.repositories.MatchRepository;
import com.upc.courtly.notifications.domain.model.aggregates.Notification;
import com.upc.courtly.notifications.domain.model.valueobjects.NotificationType;
import com.upc.courtly.notifications.infrastructure.persistence.jpa.repositories.NotificationRepository;
import com.upc.courtly.users.infrastructure.persistence.jpa.repositories.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MatchJoinRequestCommandServiceImpl implements MatchJoinRequestCommandService {
    private final MatchJoinRequestRepository matchJoinRequestRepository;
    private final MatchRepository matchRepository;
    private final UserProfileRepository userProfileRepository;
    private final NotificationRepository notificationRepository;

    public MatchJoinRequestCommandServiceImpl(MatchJoinRequestRepository matchJoinRequestRepository,
                                              MatchRepository matchRepository,
                                              UserProfileRepository userProfileRepository,
                                              NotificationRepository notificationRepository) {
        this.matchJoinRequestRepository = matchJoinRequestRepository;
        this.matchRepository = matchRepository;
        this.userProfileRepository = userProfileRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Optional<MatchJoinRequest> handle(CreateMatchJoinRequestCommand command) {
        var match = matchRepository.findById(command.matchId())
                .orElseThrow(() -> new IllegalArgumentException("Match with id " + command.matchId() + " not found"));
        var requester = userProfileRepository.findById(command.requesterId())
                .orElseThrow(() -> new IllegalArgumentException("User with id " + command.requesterId() + " not found"));

        if (match.hasParticipant(requester)) {
            throw new IllegalArgumentException("User is already participating in this match");
        }
        if (match.getStatus() == MatchStatus.CANCELLED || match.getStatus() == MatchStatus.COMPLETED) {
            throw new IllegalArgumentException("This match is not open for new participants");
        }
        if (match.getCurrentPlayers() >= match.getMaxPlayers()) {
            throw new IllegalArgumentException("This match has no available slots");
        }

        boolean alreadyRequested = matchJoinRequestRepository.findByMatchId(match.getId()).stream()
                .anyMatch(existing -> existing.getStatus() == MatchJoinRequestStatus.PENDING
                        && existing.getRequester().getId().equals(requester.getId()));
        if (alreadyRequested) {
            throw new IllegalArgumentException("A pending join request already exists for this user");
        }

        var joinRequest = new MatchJoinRequest(match, requester);
        var createdRequest = matchJoinRequestRepository.save(joinRequest);

        for (var participant : match.getParticipants()) {
            notificationRepository.save(new Notification(
                    "Solicitud para unirse al partido",
                    requester.getName() + " quiere unirse a tu partido y necesita tu aprobación.",
                    NotificationType.MATCH_JOIN_REQUESTED,
                    false,
                    "MATCH_JOIN_REQUEST",
                    createdRequest.getId(),
                    participant
            ));
        }
        notificationRepository.save(new Notification(
                "Solicitud enviada",
                "Tu solicitud para unirte al partido fue enviada y espera la aprobación de los participantes.",
                NotificationType.MATCH_JOIN_REQUESTED,
                false,
                "MATCH_JOIN_REQUEST",
                createdRequest.getId(),
                requester
        ));

        return Optional.of(createdRequest);
    }

    @Override
    public Optional<MatchJoinRequest> handle(ApproveMatchJoinRequestCommand command) {
        var joinRequest = matchJoinRequestRepository.findById(command.joinRequestId())
                .orElseThrow(() -> new IllegalArgumentException("Join request with id " + command.joinRequestId() + " not found"));
        var approver = userProfileRepository.findById(command.approverId())
                .orElseThrow(() -> new IllegalArgumentException("User with id " + command.approverId() + " not found"));

        joinRequest.approve(approver);

        if (joinRequest.isFullyApproved()) {
            var match = joinRequest.getMatch();
            match.join(joinRequest.getRequester());
            matchRepository.save(match);
            joinRequest.markApproved();

            notificationRepository.save(new Notification(
                    "¡Consenso alcanzado!",
                    "Todos los participantes aprobaron tu solicitud. ¡Ya formas parte del partido!",
                    NotificationType.MATCH_JOIN_APPROVED,
                    false,
                    "MATCH_JOIN_REQUEST",
                    joinRequest.getId(),
                    joinRequest.getRequester()
            ));
        }

        var savedRequest = matchJoinRequestRepository.save(joinRequest);
        return Optional.of(savedRequest);
    }
}
