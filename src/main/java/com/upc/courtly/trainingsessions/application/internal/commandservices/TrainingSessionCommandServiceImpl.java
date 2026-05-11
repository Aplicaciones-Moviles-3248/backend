package com.upc.courtly.trainingsessions.application.internal.commandservices;

import com.upc.courtly.availabilities.domain.model.valueobjects.AvailabilityStatus;
import com.upc.courtly.availabilities.infrastructure.persistence.jpa.repositories.AvailabilityRepository;
import com.upc.courtly.bookings.domain.model.valueobjects.BookingStatus;
import com.upc.courtly.bookings.infrastructure.persistence.jpa.repositories.BookingRepository;
import com.upc.courtly.coaches.infrastructure.persistence.jpa.repositories.CoachRepository;
import com.upc.courtly.courts.infrastructure.persistence.jpa.repositories.CourtRepository;
import com.upc.courtly.notifications.domain.model.aggregates.Notification;
import com.upc.courtly.notifications.domain.model.valueobjects.NotificationType;
import com.upc.courtly.notifications.infrastructure.persistence.jpa.repositories.NotificationRepository;
import com.upc.courtly.trainingsessions.domain.model.aggregates.TrainingSession;
import com.upc.courtly.trainingsessions.domain.model.commands.*;
import com.upc.courtly.trainingsessions.domain.model.valueobjects.TrainingSessionStatus;
import com.upc.courtly.trainingsessions.domain.services.TrainingSessionCommandService;
import com.upc.courtly.trainingsessions.infrastructure.persistence.jpa.repositories.TrainingSessionRepository;
import com.upc.courtly.users.infrastructure.persistence.jpa.repositories.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainingSessionCommandServiceImpl implements TrainingSessionCommandService {
    private final TrainingSessionRepository trainingSessionRepository;
    private final UserProfileRepository userProfileRepository;
    private final CoachRepository coachRepository;
    private final CourtRepository courtRepository;
    private final AvailabilityRepository availabilityRepository;
    private final BookingRepository bookingRepository;
    private final NotificationRepository notificationRepository;

    public TrainingSessionCommandServiceImpl(TrainingSessionRepository trainingSessionRepository,
                                             UserProfileRepository userProfileRepository,
                                             CoachRepository coachRepository,
                                             CourtRepository courtRepository,
                                             AvailabilityRepository availabilityRepository,
                                             BookingRepository bookingRepository,
                                             NotificationRepository notificationRepository) {
        this.trainingSessionRepository = trainingSessionRepository;
        this.userProfileRepository = userProfileRepository;
        this.coachRepository = coachRepository;
        this.courtRepository = courtRepository;
        this.availabilityRepository = availabilityRepository;
        this.bookingRepository = bookingRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Optional<TrainingSession> handle(CreateTrainingSessionCommand command) {
        var player = userProfileRepository.findById(command.playerId()).orElseThrow(() -> new IllegalArgumentException("Player with id " + command.playerId() + " not found"));
        var coach = coachRepository.findById(command.coachId()).orElseThrow(() -> new IllegalArgumentException("Coach with id " + command.coachId() + " not found"));
        var court = courtRepository.findById(command.courtId()).orElseThrow(() -> new IllegalArgumentException("Court with id " + command.courtId() + " not found"));
        var availability = availabilityRepository.findById(command.availabilityId()).orElseThrow(() -> new IllegalArgumentException("Availability with id " + command.availabilityId() + " not found"));

        if (!availability.getCoach().getId().equals(command.coachId())) {
            throw new IllegalArgumentException("Availability does not belong to the selected coach");
        }
        if (availability.getStatus() != AvailabilityStatus.AVAILABLE) {
            throw new IllegalArgumentException("Availability is not open for reservation");
        }
        if (bookingRepository.existsOverlappingBooking(command.courtId(), command.startTime(), command.endTime(),
                List.of(BookingStatus.PENDING_PAYMENT, BookingStatus.CONFIRMED))) {
            throw new IllegalArgumentException("Court is already booked for the selected time");
        }
        if (trainingSessionRepository.existsOverlappingCourtAssignment(command.courtId(), command.startTime(), command.endTime(),
                List.of(TrainingSessionStatus.ACCEPTED, TrainingSessionStatus.COMPLETED))) {
            throw new IllegalArgumentException("Court is already assigned to another training session");
        }

        var trainingSession = new TrainingSession(player, coach, court, availability, command.startTime(), command.endTime(), command.price());
        var createdTrainingSession = trainingSessionRepository.save(trainingSession);
        if (coach.getUser() != null) {
            userProfileRepository.findByUserId(coach.getUser().getId()).ifPresent(coachProfileOwner ->
                    notificationRepository.save(new Notification(
                            "Training session requested",
                            "A new training session request is waiting for your review.",
                            NotificationType.TRAINING_SESSION_REQUESTED,
                            false,
                            "TRAINING_SESSION",
                            createdTrainingSession.getId(),
                            coachProfileOwner
                    ))
            );
        }
        notificationRepository.save(new Notification(
                "Training session requested",
                "Your training session request was created successfully.",
                NotificationType.TRAINING_SESSION_REQUESTED,
                false,
                "TRAINING_SESSION",
                createdTrainingSession.getId(),
                player
        ));
        return Optional.of(createdTrainingSession);
    }

    @Override
    public Optional<TrainingSession> handle(AcceptTrainingSessionCommand command) {
        return trainingSessionRepository.findById(command.trainingSessionId()).map(trainingSession -> {
            if (trainingSession.getStatus() != TrainingSessionStatus.PENDING) {
                throw new IllegalArgumentException("Only pending training sessions can be accepted");
            }
            if (bookingRepository.existsOverlappingBooking(trainingSession.getCourt().getId(), trainingSession.getStartTime(), trainingSession.getEndTime(),
                    List.of(BookingStatus.PENDING_PAYMENT, BookingStatus.CONFIRMED))) {
                throw new IllegalArgumentException("Court is no longer available");
            }
            if (trainingSessionRepository.existsOverlappingCourtAssignment(trainingSession.getCourt().getId(), trainingSession.getStartTime(), trainingSession.getEndTime(),
                    List.of(TrainingSessionStatus.ACCEPTED, TrainingSessionStatus.COMPLETED))) {
                throw new IllegalArgumentException("Court is already assigned to another accepted training session");
            }
            trainingSession.accept();
            trainingSession.getAvailability().setStatus(AvailabilityStatus.RESERVED);
            notificationRepository.save(new Notification(
                    "Training session accepted",
                    "Your training session request was accepted.",
                    NotificationType.TRAINING_SESSION_ACCEPTED,
                    false,
                    "TRAINING_SESSION",
                    trainingSession.getId(),
                    trainingSession.getPlayer()
            ));
            return trainingSessionRepository.save(trainingSession);
        });
    }

    @Override
    public Optional<TrainingSession> handle(RejectTrainingSessionCommand command) {
        return trainingSessionRepository.findById(command.trainingSessionId()).map(trainingSession -> {
            if (trainingSession.getStatus() != TrainingSessionStatus.PENDING) {
                throw new IllegalArgumentException("Only pending training sessions can be rejected");
            }
            trainingSession.reject(command.reason());
            notificationRepository.save(new Notification(
                    "Training session rejected",
                    "Your training session request was rejected.",
                    NotificationType.TRAINING_SESSION_REJECTED,
                    false,
                    "TRAINING_SESSION",
                    trainingSession.getId(),
                    trainingSession.getPlayer()
            ));
            return trainingSessionRepository.save(trainingSession);
        });
    }

    @Override
    public Optional<TrainingSession> handle(CancelTrainingSessionCommand command) {
        return trainingSessionRepository.findById(command.trainingSessionId()).map(trainingSession -> {
            trainingSession.cancel(command.reason());
            if (trainingSession.getAvailability().getStatus() == AvailabilityStatus.RESERVED) {
                trainingSession.getAvailability().setStatus(AvailabilityStatus.AVAILABLE);
            }
            notificationRepository.save(new Notification(
                    "Training session cancelled",
                    "A training session was cancelled.",
                    NotificationType.TRAINING_SESSION_CANCELLED,
                    false,
                    "TRAINING_SESSION",
                    trainingSession.getId(),
                    trainingSession.getPlayer()
            ));
            return trainingSessionRepository.save(trainingSession);
        });
    }

    @Override
    public Optional<TrainingSession> handle(CompleteTrainingSessionCommand command) {
        return trainingSessionRepository.findById(command.trainingSessionId()).map(trainingSession -> {
            if (trainingSession.getStatus() != TrainingSessionStatus.ACCEPTED) {
                throw new IllegalArgumentException("Only accepted training sessions can be completed");
            }
            trainingSession.complete();
            notificationRepository.save(new Notification(
                    "Review enabled",
                    "You can now review your coach and court.",
                    NotificationType.REVIEW_ENABLED,
                    false,
                    "TRAINING_SESSION",
                    trainingSession.getId(),
                    trainingSession.getPlayer()
            ));
            return trainingSessionRepository.save(trainingSession);
        });
    }

    @Override
    public void handle(DeleteTrainingSessionCommand command) {
        if (!trainingSessionRepository.existsById(command.trainingSessionId())) {
            throw new IllegalArgumentException("Training session with id " + command.trainingSessionId() + " not found");
        }
        trainingSessionRepository.deleteById(command.trainingSessionId());
    }
}
