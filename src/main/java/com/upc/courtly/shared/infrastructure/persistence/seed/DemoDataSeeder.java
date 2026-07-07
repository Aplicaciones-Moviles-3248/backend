package com.upc.courtly.shared.infrastructure.persistence.seed;

import com.upc.courtly.availabilities.domain.model.aggregates.Availability;
import com.upc.courtly.availabilities.domain.model.valueobjects.AvailabilityStatus;
import com.upc.courtly.availabilities.infrastructure.persistence.jpa.repositories.AvailabilityRepository;
import com.upc.courtly.bookings.domain.model.aggregates.Booking;
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
import com.upc.courtly.matches.domain.model.valueobjects.MatchStatus;
import com.upc.courtly.matches.infrastructure.persistence.jpa.repositories.MatchRepository;
import com.upc.courtly.notifications.domain.model.aggregates.Notification;
import com.upc.courtly.notifications.domain.model.valueobjects.NotificationType;
import com.upc.courtly.notifications.infrastructure.persistence.jpa.repositories.NotificationRepository;
import com.upc.courtly.payments.domain.model.aggregates.Payment;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final CoachRepository coachRepository;
    private final CourtRepository courtRepository;
    private final AvailabilityRepository availabilityRepository;
    private final MatchRepository matchRepository;
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
        return userProfileRepository.findByUserId(user.getId()).orElseGet(() ->
                userProfileRepository.save(new UserProfile(name, email, phone, user)));
    }

    private Coach ensureCoach(String username, String name, String expertise, String phone) {
        var role = roleRepository.findByName(Roles.ROLE_INSTRUCTOR)
                .orElseGet(() -> roleRepository.save(new Role(Roles.ROLE_INSTRUCTOR)));
        var user = userRepository.findByUsername(username).orElseGet(() ->
                userRepository.save(new User(username, hashingService.encode(DEMO_PASSWORD), List.of(role))));
        return coachRepository.findByUserId(user.getId()).orElseGet(() ->
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
}
