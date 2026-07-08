package com.upc.courtly.shared.infrastructure.persistence.seed;

import com.upc.courtly.availabilities.domain.model.aggregates.Availability;
import com.upc.courtly.availabilities.domain.model.valueobjects.AvailabilityStatus;
import com.upc.courtly.availabilities.infrastructure.persistence.jpa.repositories.AvailabilityRepository;
import com.upc.courtly.bookings.domain.model.aggregates.Booking;
import com.upc.courtly.bookings.domain.model.valueobjects.BookingStatus;
import com.upc.courtly.bookings.infrastructure.persistence.jpa.repositories.BookingRepository;
import com.upc.courtly.coaches.domain.model.aggregates.Coach;
import com.upc.courtly.coaches.infrastructure.persistence.jpa.repositories.CoachRepository;
import com.upc.courtly.courts.domain.model.aggregates.Court;
import com.upc.courtly.courts.infrastructure.persistence.jpa.repositories.CourtRepository;
import com.upc.courtly.iam.domain.model.aggregates.User;
import com.upc.courtly.iam.domain.model.entities.Role;
import com.upc.courtly.iam.domain.model.valueobjects.Roles;
import com.upc.courtly.iam.infrastructure.hashing.bcrypt.BCryptHashingService;
import com.upc.courtly.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.upc.courtly.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.upc.courtly.matches.domain.model.aggregates.Match;
import com.upc.courtly.matches.domain.model.aggregates.MatchJoinRequest;
import com.upc.courtly.matches.domain.model.valueobjects.MatchStatus;
import com.upc.courtly.matches.infrastructure.persistence.jpa.repositories.MatchJoinRequestRepository;
import com.upc.courtly.matches.infrastructure.persistence.jpa.repositories.MatchRepository;
import com.upc.courtly.notifications.domain.model.aggregates.Notification;
import com.upc.courtly.notifications.domain.model.valueobjects.NotificationType;
import com.upc.courtly.notifications.infrastructure.persistence.jpa.repositories.NotificationRepository;
import com.upc.courtly.payments.domain.model.aggregates.Payment;
import com.upc.courtly.payments.domain.model.valueobjects.PaymentStatus;
import com.upc.courtly.payments.infrastructure.persistence.jpa.repositories.PaymentRepository;
import com.upc.courtly.reviews.domain.model.aggregates.Review;
import com.upc.courtly.reviews.domain.model.valueobjects.ReviewTargetType;
import com.upc.courtly.reviews.infrastructure.persistence.jpa.repositories.ReviewRepository;
import com.upc.courtly.trainingsessions.domain.model.aggregates.TrainingSession;
import com.upc.courtly.trainingsessions.domain.model.valueobjects.TrainingSessionStatus;
import com.upc.courtly.trainingsessions.infrastructure.persistence.jpa.repositories.TrainingSessionRepository;
import com.upc.courtly.users.domain.model.aggregates.UserProfile;
import com.upc.courtly.users.infrastructure.persistence.jpa.repositories.UserProfileRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Populates the database with varied, realistic-looking demo data (players, coaches,
 * courts, matches, bookings, training sessions, reviews, payments, notifications) so
 * the apps have something to show in a live demo instead of empty screens. Runs once:
 * every entity is looked up by a stable demo username/email/name first, so re-running
 * on every deploy never duplicates rows.
 */
@Component
@Profile("!test")
@Order(2)
@Transactional
public class DemoDataSeeder implements CommandLineRunner {

    private static final String DEMO_PASSWORD = "123456";

    // Labels/hours for the week-spread demo data (Monday..Sunday, matching DayOfWeek.values() order).
    private static final String[] WEEKDAY_LABELS =
            {"Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"};
    private static final int[] SESSION_HOURS = {9, 18, 11, 16, 8, 14, 19};
    private static final int[] BOOKING_HOURS = {19, 8, 12, 10, 20, 9, 17};
    private static final int[] MATCH_HOURS = {20, 9, 18, 19, 21, 11, 17};

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final CoachRepository coachRepository;
    private final CourtRepository courtRepository;
    private final AvailabilityRepository availabilityRepository;
    private final MatchRepository matchRepository;
    private final MatchJoinRequestRepository matchJoinRequestRepository;
    private final BookingRepository bookingRepository;
    private final TrainingSessionRepository trainingSessionRepository;
    private final ReviewRepository reviewRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationRepository notificationRepository;
    private final BCryptHashingService hashingService;

    public DemoDataSeeder(RoleRepository roleRepository,
                          UserRepository userRepository,
                          UserProfileRepository userProfileRepository,
                          CoachRepository coachRepository,
                          CourtRepository courtRepository,
                          AvailabilityRepository availabilityRepository,
                          MatchRepository matchRepository,
                          MatchJoinRequestRepository matchJoinRequestRepository,
                          BookingRepository bookingRepository,
                          TrainingSessionRepository trainingSessionRepository,
                          ReviewRepository reviewRepository,
                          PaymentRepository paymentRepository,
                          NotificationRepository notificationRepository,
                          BCryptHashingService hashingService) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.coachRepository = coachRepository;
        this.courtRepository = courtRepository;
        this.availabilityRepository = availabilityRepository;
        this.matchRepository = matchRepository;
        this.matchJoinRequestRepository = matchJoinRequestRepository;
        this.bookingRepository = bookingRepository;
        this.trainingSessionRepository = trainingSessionRepository;
        this.reviewRepository = reviewRepository;
        this.paymentRepository = paymentRepository;
        this.notificationRepository = notificationRepository;
        this.hashingService = hashingService;
    }

    @Override
    public void run(String... args) {
        var courts = ensureCourts();

        var players = List.of(
                ensurePlayer("demo_player1", "Juan Torres", "juan.torres@courtly.app", "+51 988 111 222"),
                ensurePlayer("demo_player2", "Maria Quispe", "maria.quispe@courtly.app", "+51 988 222 333"),
                ensurePlayer("demo_player3", "Diego Alvarado", "diego.alvarado@courtly.app", "+51 988 333 444"),
                ensurePlayer("demo_player4", "Camila Rios", "camila.rios@courtly.app", "+51 988 444 555"),
                ensurePlayer("demo_player5", "Andres Paredes", "andres.paredes@courtly.app", "+51 988 555 666")
        );

        var coaches = List.of(
                ensureCoach("demo_coach1", "Fabricio Ruiz", "Tenis Avanzado & Tactica", "+51 987 654 321"),
                ensureCoach("demo_coach2", "Sofia Mendoza", "Padel Iniciacion & Tecnica", "+51 912 345 678"),
                ensureCoach("demo_coach3", "Carlos Bacca", "Futbol & Preparacion Fisica", "+51 955 443 322")
        );

        var availabilities = ensureAvailabilities(coaches);
        var trainingSessions = ensureTrainingSessions(players, coaches, courts, availabilities);
        ensureMatches(players, courts);
        var bookings = ensureBookings(players, courts);
        ensureReviews(players, courts, coaches, bookings, trainingSessions);
        ensurePayments(players, bookings, trainingSessions);
        ensureNotifications(players);

        ensureWeeklyDemoData(players, coaches, courts);
    }

    /**
     * Adds a richer, week-spread layer on top of the base demo data above: training
     * sessions and matches landing on distinct days Monday..Sunday of the CURRENT week
     * (mixing PENDING/ACCEPTED/COMPLETED so the app always shows "something happening
     * this week"), matching bookings/payments, and visible match join-request activity.
     * Each helper below has its own idempotency guard (overlap checks / logical-key
     * lookups) independent from the base data's count()==0 guards above, so this can
     * run again on a later date and seed a new week without erroring or duplicating
     * same-day data.
     */
    private void ensureWeeklyDemoData(List<UserProfile> players, List<Coach> coaches, List<Court> courts) {
        var today = LocalDate.now();
        var startOfWeek = today.with(DayOfWeek.MONDAY);

        var weeklySessions = ensureWeeklyTrainingSessions(players, coaches, courts, startOfWeek, today);
        var weeklyBookings = ensureWeeklyBookings(players, courts, startOfWeek, today);
        ensureWeeklyPayments(weeklySessions, weeklyBookings);

        var weeklyMatches = ensureWeeklyMatches(players, courts, startOfWeek, today);
        ensureWeeklyJoinRequests(players, weeklyMatches);
    }

    private List<Court> ensureCourts() {
        if (courtRepository.count() > 0) {
            return courtRepository.findAll();
        }
        record Seed(String name, String location, String type, String imageUrl, BigDecimal price) {}
        var seeds = List.of(
                new Seed("Arena Norte", "San Isidro", "Futbol 7", "https://images.unsplash.com/photo-1574629810360-7efbbe195018", new BigDecimal("120.00")),
                new Seed("Green Point Club", "Miraflores", "Padel", "https://images.unsplash.com/photo-1622279457486-62dcc4a431d6", new BigDecimal("95.00")),
                new Seed("Sport Center Lima", "Surco", "Futbol 6", "https://images.unsplash.com/photo-1551958219-acbc608c6377", new BigDecimal("110.00")),
                new Seed("Elite Padel Arena", "La Molina", "Padel", "https://images.unsplash.com/photo-1554068865-24cecd4e34b8", new BigDecimal("100.00")),
                new Seed("Cancha Central", "San Miguel", "Futbol 7", "https://images.unsplash.com/photo-1522778119026-d647f0596c20", new BigDecimal("85.00")),
                new Seed("Tennis Club Park", "Miraflores", "Tenis", "https://images.unsplash.com/photo-1595435934249-5df7ed86e1c0", new BigDecimal("90.00")),
                new Seed("Depor Plaza", "Los Olivos", "Futbol 5", "https://images.unsplash.com/photo-1431324155629-1a6deb1dec8d", new BigDecimal("70.00")),
                new Seed("Urban Padel Club", "Barranco", "Padel", "https://images.unsplash.com/photo-1626224583764-f87db24ac4ea", new BigDecimal("105.00"))
        );
        return seeds.stream()
                .map(seed -> courtRepository.save(new Court(seed.name(), seed.location(), seed.type(), seed.imageUrl(), seed.price())))
                .toList();
    }

    private UserProfile ensurePlayer(String username, String name, String email, String phone) {
        var role = roleRepository.findByName(Roles.ROLE_USER)
                .orElseGet(() -> roleRepository.save(new Role(Roles.ROLE_USER)));
        var user = userRepository.findByUsername(username).orElseGet(() ->
                userRepository.save(new User(username, hashingService.encode(DEMO_PASSWORD), List.of(role))));
        var existingByUser = userProfileRepository.findByUserId(user.getId());
        if (existingByUser.isPresent()) {
            return existingByUser.get();
        }
        // email is unique; avoid a duplicate-key crash-loop if it was already taken.
        return userProfileRepository.findByEmail(email).orElseGet(() ->
                userProfileRepository.save(new UserProfile(name, email, phone, user)));
    }

    private Coach ensureCoach(String username, String name, String expertise, String phone) {
        var role = roleRepository.findByName(Roles.ROLE_INSTRUCTOR)
                .orElseGet(() -> roleRepository.save(new Role(Roles.ROLE_INSTRUCTOR)));
        var user = userRepository.findByUsername(username).orElseGet(() ->
                userRepository.save(new User(username, hashingService.encode(DEMO_PASSWORD), List.of(role))));
        var existingByUser = coachRepository.findByUserId(user.getId());
        if (existingByUser.isPresent()) {
            return existingByUser.get();
        }
        // Coach.name is unique; a coach with this display name may already exist under a
        // different account (e.g. manually created before this seeder ran) — reuse it
        // rather than crash-looping the whole app on a duplicate-key error every boot.
        return coachRepository.findByName(name).orElseGet(() ->
                coachRepository.save(new Coach(name, expertise, phone, user)));
    }

    private List<Availability> ensureAvailabilities(List<Coach> coaches) {
        if (availabilityRepository.count() > 0) {
            return availabilityRepository.findAll();
        }
        var tomorrow = LocalDate.now().plusDays(1);
        return List.of(
                availabilityRepository.save(new Availability(tomorrow, LocalTime.of(9, 0), LocalTime.of(10, 0), AvailabilityStatus.AVAILABLE, coaches.get(0))),
                availabilityRepository.save(new Availability(tomorrow, LocalTime.of(17, 0), LocalTime.of(18, 0), AvailabilityStatus.AVAILABLE, coaches.get(0))),
                availabilityRepository.save(new Availability(tomorrow.plusDays(1), LocalTime.of(10, 0), LocalTime.of(11, 0), AvailabilityStatus.AVAILABLE, coaches.get(1))),
                availabilityRepository.save(new Availability(tomorrow.plusDays(1), LocalTime.of(16, 0), LocalTime.of(17, 0), AvailabilityStatus.RESERVED, coaches.get(1))),
                availabilityRepository.save(new Availability(tomorrow.plusDays(2), LocalTime.of(18, 0), LocalTime.of(19, 0), AvailabilityStatus.AVAILABLE, coaches.get(2))),
                availabilityRepository.save(new Availability(tomorrow.minusDays(3), LocalTime.of(9, 0), LocalTime.of(10, 0), AvailabilityStatus.RESERVED, coaches.get(2)))
        );
    }

    private List<TrainingSession> ensureTrainingSessions(List<UserProfile> players, List<Coach> coaches,
                                                          List<Court> courts, List<Availability> availabilities) {
        if (trainingSessionRepository.count() > 0) {
            return trainingSessionRepository.findAll();
        }
        var now = LocalDateTime.now();
        var pending = new TrainingSession(players.get(0), coaches.get(1), courts.get(1), availabilities.get(3),
                now.plusDays(1).withHour(16), now.plusDays(1).withHour(17), new BigDecimal("95.00"));

        var accepted = new TrainingSession(players.get(1), coaches.get(0), courts.get(5), availabilities.get(0),
                now.plusDays(1).withHour(9), now.plusDays(1).withHour(10), new BigDecimal("90.00"));
        accepted.accept();

        var completed = new TrainingSession(players.get(2), coaches.get(2), courts.get(0), availabilities.get(5),
                now.minusDays(3).withHour(9), now.minusDays(3).withHour(10), new BigDecimal("70.00"));
        completed.accept();
        completed.complete();

        var rejected = new TrainingSession(players.get(3), coaches.get(0), courts.get(5), availabilities.get(1),
                now.plusDays(1).withHour(17), now.plusDays(1).withHour(18), new BigDecimal("90.00"));
        rejected.reject("Horario ya comprometido con otro alumno.");

        return List.of(
                trainingSessionRepository.save(pending),
                trainingSessionRepository.save(accepted),
                trainingSessionRepository.save(completed),
                trainingSessionRepository.save(rejected)
        );
    }

    private void ensureMatches(List<UserProfile> players, List<Court> courts) {
        if (matchRepository.count() > 0) {
            return;
        }
        var now = LocalDateTime.now();

        var openMatch = new Match("Fulbito de los viernes", "Partido casual, todos los niveles bienvenidos.",
                now.plusDays(2).withHour(19), MatchStatus.OPEN, 10, 1, courts.get(0), players.get(0));
        openMatch.join(players.get(1));
        openMatch.join(players.get(2));

        var padelMatch = new Match("Padel doble competitivo", "Buscamos pareja para partido de nivel intermedio.",
                now.plusDays(1).withHour(18), MatchStatus.OPEN, 4, 1, courts.get(1), players.get(3));
        padelMatch.join(players.get(4));

        var tennisMatch = new Match("Tenis singles", "Partido amistoso de tenis, cualquier nivel.",
                now.plusDays(3).withHour(8), MatchStatus.OPEN, 2, 1, courts.get(5), players.get(1));

        var fullMatch = new Match("Futbol 6 nocturno", "Partido cerrado, ya tenemos equipo completo.",
                now.plusDays(4).withHour(20), MatchStatus.OPEN, 4, 1, courts.get(2), players.get(2));
        fullMatch.join(players.get(0));
        fullMatch.join(players.get(3));
        fullMatch.join(players.get(4));

        matchRepository.save(openMatch);
        matchRepository.save(padelMatch);
        matchRepository.save(tennisMatch);
        matchRepository.save(fullMatch);
    }

    private List<Booking> ensureBookings(List<UserProfile> players, List<Court> courts) {
        if (bookingRepository.count() > 0) {
            return bookingRepository.findAll();
        }
        var now = LocalDateTime.now();

        var confirmed = new Booking(now.plusDays(1).withHour(19), now.plusDays(1).withHour(20), players.get(0), courts.get(0));
        confirmed.confirm();

        var completed = new Booking(now.minusDays(5).withHour(18), now.minusDays(5).withHour(19), players.get(1), courts.get(3));
        completed.confirm();
        completed.complete();

        var pending = new Booking(now.plusDays(2).withHour(20), now.plusDays(2).withHour(21), players.get(2), courts.get(6));

        var cancelled = new Booking(now.plusDays(1).withHour(10), now.plusDays(1).withHour(11), players.get(3), courts.get(4));
        cancelled.cancel();

        return List.of(
                bookingRepository.save(confirmed),
                bookingRepository.save(completed),
                bookingRepository.save(pending),
                bookingRepository.save(cancelled)
        );
    }

    private void ensureReviews(List<UserProfile> players, List<Court> courts, List<Coach> coaches,
                               List<Booking> bookings, List<TrainingSession> trainingSessions) {
        if (reviewRepository.count() > 0) {
            return;
        }
        var completedBooking = bookings.get(1);
        var completedSession = trainingSessions.get(2);

        reviewRepository.save(new Review(5, "Excelente cancha, buen mantenimiento y facil de reservar.",
                ReviewTargetType.COURT.name(), courts.get(3).getId(), ReviewTargetType.COURT,
                completedBooking.getId(), null, players.get(1)));

        reviewRepository.save(new Review(4, "Buena iluminacion y ambiente tranquilo para jugar de noche.",
                ReviewTargetType.COURT.name(), courts.get(0).getId(), ReviewTargetType.COURT,
                bookings.get(0).getId(), null, players.get(0)));

        reviewRepository.save(new Review(5, "El entrenador Carlos es muy paciente y explica bien la tecnica.",
                ReviewTargetType.COACH.name(), coaches.get(2).getId(), ReviewTargetType.COACH,
                null, completedSession.getId(), players.get(2)));
    }

    private void ensurePayments(List<UserProfile> players, List<Booking> bookings, List<TrainingSession> trainingSessions) {
        if (paymentRepository.count() > 0) {
            return;
        }
        paymentRepository.save(new Payment(new BigDecimal("120.00"), players.get(0), bookings.get(0), null));
        paymentRepository.save(new Payment(new BigDecimal("100.00"), players.get(1), bookings.get(1), null));
        paymentRepository.save(new Payment(new BigDecimal("90.00"), players.get(1), null, trainingSessions.get(1)));
        paymentRepository.save(new Payment(new BigDecimal("70.00"), players.get(2), null, trainingSessions.get(2)));
    }

    private void ensureNotifications(List<UserProfile> players) {
        if (notificationRepository.count() > 0) {
            return;
        }
        notificationRepository.save(new Notification("Reserva confirmada", "Tu reserva en Arena Norte ha sido confirmada.",
                NotificationType.BOOKING_CONFIRMED, false, "BOOKING", null, players.get(0)));
        notificationRepository.save(new Notification("Entrenamiento aceptado", "Fabricio Ruiz acepto tu solicitud de entrenamiento.",
                NotificationType.TRAINING_SESSION_ACCEPTED, false, "TRAINING_SESSION", null, players.get(1)));
        notificationRepository.save(new Notification("Ya puedes calificar", "Completa tu resena sobre tu ultima sesion de entrenamiento.",
                NotificationType.REVIEW_ENABLED, true, "TRAINING_SESSION", null, players.get(2)));
    }

    /**
     * One training session per weekday (Monday..Sunday of the current week), each with
     * its own freshly created Availability. Past days land COMPLETED, today lands
     * ACCEPTED (in progress / about to happen), the soonest upcoming day also lands
     * ACCEPTED (an "active" confirmed session coming up), the rest stay PENDING.
     * Guarded per-day via the overlap-check repository methods, so a same-day re-run
     * finds the slot already taken and skips it, while a later week (different dates)
     * seeds fresh rows.
     */
    private List<TrainingSession> ensureWeeklyTrainingSessions(List<UserProfile> players, List<Coach> coaches,
                                                                List<Court> courts, LocalDate startOfWeek, LocalDate today) {
        var activeStatuses = List.of(TrainingSessionStatus.PENDING, TrainingSessionStatus.ACCEPTED, TrainingSessionStatus.COMPLETED);
        var sessions = new ArrayList<TrainingSession>();
        var soonestFutureAssigned = false;

        for (int offset = 0; offset < 7; offset++) {
            var date = startOfWeek.plusDays(offset);
            var coach = coaches.get(offset % coaches.size());
            var player = players.get(offset % players.size());
            var court = courts.get(offset % courts.size());
            var start = date.atTime(SESSION_HOURS[offset], 0);
            var end = start.plusHours(1);

            if (trainingSessionRepository.existsOverlappingCourtAssignment(court.getId(), start, end, activeStatuses)) {
                continue; // court already committed this slot (base seed data or an earlier run this week)
            }
            if (availabilityRepository.existsOverlappingAvailability(coach.getId(), date, start.toLocalTime(), end.toLocalTime(), null)) {
                continue; // coach already has an availability row for this slot
            }

            var availability = availabilityRepository.save(new Availability(date, start.toLocalTime(), end.toLocalTime(),
                    AvailabilityStatus.RESERVED, coach));
            var session = new TrainingSession(player, coach, court, availability, start, end,
                    new BigDecimal("80.00").add(BigDecimal.valueOf(offset * 5L)));

            if (date.isBefore(today)) {
                session.accept();
                session.complete();
            } else if (date.isEqual(today)) {
                session.accept();
            } else if (!soonestFutureAssigned) {
                session.accept();
                soonestFutureAssigned = true;
            } // else: stays PENDING, a future day still awaiting coach acceptance

            sessions.add(trainingSessionRepository.save(session));
        }
        return sessions;
    }

    /**
     * One court booking per weekday of the current week, following the same
     * past/today/soonest-future/rest-pending status spread as the training sessions
     * above. Guarded via the booking and training-session overlap-check methods so a
     * same-day re-run is a no-op and a new week seeds fresh rows.
     */
    private List<Booking> ensureWeeklyBookings(List<UserProfile> players, List<Court> courts,
                                                LocalDate startOfWeek, LocalDate today) {
        var activeBookingStatuses = List.of(BookingStatus.PENDING_PAYMENT, BookingStatus.CONFIRMED, BookingStatus.COMPLETED);
        var activeSessionStatuses = List.of(TrainingSessionStatus.PENDING, TrainingSessionStatus.ACCEPTED, TrainingSessionStatus.COMPLETED);
        var bookings = new ArrayList<Booking>();
        var soonestFutureAssigned = false;

        for (int offset = 0; offset < 7; offset++) {
            var date = startOfWeek.plusDays(offset);
            var player = players.get((offset + 2) % players.size());
            var court = courts.get((offset + 3) % courts.size());
            var start = date.atTime(BOOKING_HOURS[offset], 0);
            var end = start.plusHours(1);

            if (bookingRepository.existsOverlappingBooking(court.getId(), start, end, activeBookingStatuses)) {
                continue;
            }
            if (trainingSessionRepository.existsOverlappingCourtAssignment(court.getId(), start, end, activeSessionStatuses)) {
                continue;
            }

            var booking = new Booking(start, end, player, court);
            if (date.isBefore(today)) {
                booking.confirm();
                booking.complete();
            } else if (date.isEqual(today)) {
                booking.confirm();
            } else if (!soonestFutureAssigned) {
                booking.confirm();
                soonestFutureAssigned = true;
            } // else: stays PENDING_PAYMENT

            bookings.add(bookingRepository.save(booking));
        }
        return bookings;
    }

    /**
     * Completed/confirmed weekly sessions and bookings should logically already be
     * paid for; pending ones should not. Guarded via the existing existsBy...PaymentStatus
     * lookups so re-running never double-pays the same booking/session.
     */
    private void ensureWeeklyPayments(List<TrainingSession> weeklySessions, List<Booking> weeklyBookings) {
        for (var session : weeklySessions) {
            var payable = session.getStatus() == TrainingSessionStatus.ACCEPTED
                    || session.getStatus() == TrainingSessionStatus.COMPLETED;
            if (payable && !paymentRepository.existsByTrainingSessionIdAndPaymentStatus(session.getId(), PaymentStatus.COMPLETED)) {
                paymentRepository.save(new Payment(session.getPrice(), session.getPlayer(), null, session));
            }
        }
        for (var booking : weeklyBookings) {
            var payable = booking.getStatus() == BookingStatus.CONFIRMED || booking.getStatus() == BookingStatus.COMPLETED;
            if (payable && !paymentRepository.existsByBookingIdAndPaymentStatus(booking.getId(), PaymentStatus.COMPLETED)) {
                paymentRepository.save(new Payment(booking.getCourt().getPricePerHour(), booking.getUser(), booking, null));
            }
        }
    }

    /**
     * One open match per weekday of the current week. Past days are filled up and
     * marked COMPLETED, today and the soonest upcoming day get an extra participant
     * (visibly "filling up"), the remaining future days stay OPEN with just the
     * creator so there's room for the join-request demo below. Idempotency is keyed on
     * the (title, date) pair rather than a count() gate, since the base ensureMatches()
     * above already owns that gate for the original 4 matches.
     */
    private List<Match> ensureWeeklyMatches(List<UserProfile> players, List<Court> courts,
                                             LocalDate startOfWeek, LocalDate today) {
        var existingMatches = matchRepository.findAll();
        var matches = new ArrayList<Match>();
        var soonestFutureAssigned = false;

        for (int offset = 0; offset < 7; offset++) {
            var date = startOfWeek.plusDays(offset);
            var title = "Partido semanal - " + WEEKDAY_LABELS[offset];

            var alreadySeeded = existingMatches.stream()
                    .anyMatch(m -> m.getTitle().equals(title) && m.getDateTime().toLocalDate().isEqual(date));
            if (alreadySeeded) {
                continue;
            }

            var court = courts.get((offset + 5) % courts.size());
            var creator = players.get(offset % players.size());
            var dateTime = date.atTime(MATCH_HOURS[offset], 0);

            var match = new Match(title, "Partido casual de la semana, cupos limitados.", dateTime,
                    MatchStatus.OPEN, 4, 1, court, creator);

            if (date.isBefore(today)) {
                match.join(players.get((offset + 1) % players.size()));
                match.join(players.get((offset + 2) % players.size()));
                match.join(players.get((offset + 3) % players.size()));
                match.updateMatch(match.getTitle(), match.getDescription(), match.getDateTime(),
                        MatchStatus.COMPLETED, match.getMaxPlayers(), match.getCurrentPlayers());
            } else if (date.isEqual(today)) {
                match.join(players.get((offset + 1) % players.size()));
            } else if (!soonestFutureAssigned) {
                match.join(players.get((offset + 1) % players.size()));
                soonestFutureAssigned = true;
            } // else: stays OPEN with only the creator, room left for join requests

            matches.add(matchRepository.save(match));
        }
        return matches;
    }

    /**
     * Visible "invitation" activity for the week's matches: one join request left
     * PENDING (someone wants in, awaiting participant consensus) and, if a second
     * match with room is available, one that reaches full consensus and is APPROVED
     * — mirroring exactly what MatchJoinRequestCommandServiceImpl does on approval
     * (join the requester into the match, then mark the request approved).
     */
    private void ensureWeeklyJoinRequests(List<UserProfile> players, List<Match> weeklyMatches) {
        var openWithRoom = weeklyMatches.stream()
                .filter(m -> m.getStatus() == MatchStatus.OPEN && m.getCurrentPlayers() < m.getMaxPlayers())
                .toList();
        if (openWithRoom.isEmpty()) {
            return;
        }

        var pendingTarget = openWithRoom.get(0);
        ensurePendingJoinRequest(pendingTarget, pickNonParticipant(players, pendingTarget));

        if (openWithRoom.size() > 1) {
            var approvedTarget = openWithRoom.get(openWithRoom.size() - 1);
            if (!approvedTarget.getId().equals(pendingTarget.getId())) {
                ensureApprovedJoinRequest(approvedTarget, pickNonParticipant(players, approvedTarget));
            }
        }
    }

    private UserProfile pickNonParticipant(List<UserProfile> players, Match match) {
        return players.stream().filter(player -> !match.hasParticipant(player)).findFirst().orElse(null);
    }

    private boolean hasJoinRequestFrom(Match match, UserProfile requester) {
        return matchJoinRequestRepository.findByMatchId(match.getId()).stream()
                .anyMatch(request -> request.getRequester().getId().equals(requester.getId()));
    }

    private void ensurePendingJoinRequest(Match match, UserProfile requester) {
        if (requester == null || hasJoinRequestFrom(match, requester)) {
            return;
        }
        var joinRequest = matchJoinRequestRepository.save(new MatchJoinRequest(match, requester));
        notificationRepository.save(new Notification("Solicitud para unirse al partido",
                requester.getName() + " quiere unirse a \"" + match.getTitle() + "\" y espera tu aprobacion.",
                NotificationType.MATCH_JOIN_REQUESTED, false, "MATCH_JOIN_REQUEST", joinRequest.getId(),
                match.getCreatedBy()));
    }

    private void ensureApprovedJoinRequest(Match match, UserProfile requester) {
        if (requester == null || hasJoinRequestFrom(match, requester)) {
            return;
        }
        var joinRequest = new MatchJoinRequest(match, requester);
        for (var participant : match.getParticipants()) {
            joinRequest.approve(participant);
        }
        if (joinRequest.isFullyApproved()) {
            match.join(requester);
            matchRepository.save(match);
            joinRequest.markApproved();
        }
        var savedRequest = matchJoinRequestRepository.save(joinRequest);
        notificationRepository.save(new Notification("Consenso alcanzado",
                "Todos los participantes aprobaron tu solicitud para unirte a \"" + match.getTitle() + "\".",
                NotificationType.MATCH_JOIN_APPROVED, false, "MATCH_JOIN_REQUEST", savedRequest.getId(), requester));
    }
}
