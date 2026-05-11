package com.upc.courtly.payments.application.internal.commandservices;

import com.upc.courtly.bookings.domain.model.valueobjects.BookingStatus;
import com.upc.courtly.bookings.infrastructure.persistence.jpa.repositories.BookingRepository;
import com.upc.courtly.notifications.domain.model.aggregates.Notification;
import com.upc.courtly.notifications.domain.model.valueobjects.NotificationType;
import com.upc.courtly.notifications.infrastructure.persistence.jpa.repositories.NotificationRepository;
import com.upc.courtly.payments.domain.model.aggregates.Payment;
import com.upc.courtly.payments.domain.model.commands.CreatePaymentCommand;
import com.upc.courtly.payments.domain.model.valueobjects.PaymentStatus;
import com.upc.courtly.payments.domain.services.PaymentCommandService;
import com.upc.courtly.payments.infrastructure.persistence.jpa.repositories.PaymentRepository;
import com.upc.courtly.trainingsessions.domain.model.valueobjects.TrainingSessionStatus;
import com.upc.courtly.trainingsessions.infrastructure.persistence.jpa.repositories.TrainingSessionRepository;
import com.upc.courtly.users.infrastructure.persistence.jpa.repositories.UserProfileRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Optional;

@Service
public class PaymentCommandServiceImpl implements PaymentCommandService {
    private final PaymentRepository paymentRepository;
    private final UserProfileRepository userProfileRepository;
    private final BookingRepository bookingRepository;
    private final TrainingSessionRepository trainingSessionRepository;
    private final NotificationRepository notificationRepository;

    public PaymentCommandServiceImpl(PaymentRepository paymentRepository, UserProfileRepository userProfileRepository,
                                     BookingRepository bookingRepository, TrainingSessionRepository trainingSessionRepository,
                                     NotificationRepository notificationRepository) {
        this.paymentRepository = paymentRepository;
        this.userProfileRepository = userProfileRepository;
        this.bookingRepository = bookingRepository;
        this.trainingSessionRepository = trainingSessionRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Optional<Payment> handle(CreatePaymentCommand command) {
        var user = userProfileRepository.findById(command.userId()).orElseThrow(() -> new IllegalArgumentException("User with id " + command.userId() + " not found"));
        boolean hasBooking = command.bookingId() != null;
        boolean hasTrainingSession = command.trainingSessionId() != null;
        if (hasBooking == hasTrainingSession) {
            throw new IllegalArgumentException("Payment must reference exactly one target: booking or training session");
        }

        var booking = command.bookingId() == null ? null :
                bookingRepository.findById(command.bookingId()).orElseThrow(() -> new IllegalArgumentException("Booking with id " + command.bookingId() + " not found"));
        var trainingSession = command.trainingSessionId() == null ? null :
                trainingSessionRepository.findById(command.trainingSessionId()).orElseThrow(() -> new IllegalArgumentException("Training session with id " + command.trainingSessionId() + " not found"));

        if (booking != null) {
            if (!booking.getUser().getId().equals(command.userId())) {
                throw new IllegalArgumentException("Payment user does not own the booking");
            }
            if (paymentRepository.existsByBookingIdAndPaymentStatus(booking.getId(), PaymentStatus.COMPLETED)) {
                throw new IllegalArgumentException("Booking already has a completed payment");
            }
            booking.confirm();
        }

        if (trainingSession != null) {
            if (!trainingSession.getPlayer().getId().equals(command.userId())) {
                throw new IllegalArgumentException("Payment user does not own the training session");
            }
            if (trainingSession.getStatus() != TrainingSessionStatus.ACCEPTED) {
                throw new IllegalArgumentException("Training session must be accepted before payment");
            }
            if (paymentRepository.existsByTrainingSessionIdAndPaymentStatus(trainingSession.getId(), PaymentStatus.COMPLETED)) {
                throw new IllegalArgumentException("Training session already has a completed payment");
            }
        }

        var amount = booking != null ? calculateBookingAmount(booking) : trainingSession.getPrice();
        var payment = new Payment(amount, user, booking, trainingSession);
        var createdPayment = paymentRepository.save(payment);
        notificationRepository.save(new Notification(
                "Payment confirmed",
                "Your payment was processed successfully.",
                NotificationType.PAYMENT_CONFIRMED,
                false,
                booking != null ? "BOOKING" : "TRAINING_SESSION",
                booking != null ? booking.getId() : trainingSession.getId(),
                user
        ));
        if (booking != null) {
            notificationRepository.save(new Notification(
                    "Booking confirmed",
                    "Your booking is now confirmed.",
                    NotificationType.BOOKING_CONFIRMED,
                    false,
                    "BOOKING",
                    booking.getId(),
                    user
            ));
        }
        return Optional.of(createdPayment);
    }

    private BigDecimal calculateBookingAmount(com.upc.courtly.bookings.domain.model.aggregates.Booking booking) {
        var duration = Duration.between(booking.getStartTime(), booking.getEndTime());
        if (duration.isNegative() || duration.isZero()) {
            throw new IllegalArgumentException("Booking duration must be greater than zero");
        }
        var minutes = BigDecimal.valueOf(duration.toMinutes());
        var hours = minutes.divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        return booking.getCourt().getPricePerHour().multiply(hours).setScale(2, RoundingMode.HALF_UP);
    }
}

