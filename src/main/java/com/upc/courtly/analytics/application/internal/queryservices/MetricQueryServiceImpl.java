package com.upc.courtly.analytics.application.internal.queryservices;

import com.upc.courtly.analytics.domain.model.aggregates.Metric;
import com.upc.courtly.analytics.domain.model.queries.GetAllMetricsQuery;
import com.upc.courtly.analytics.domain.model.queries.GetMetricByIdQuery;
import com.upc.courtly.analytics.domain.model.valueobjects.MetricType;
import com.upc.courtly.analytics.domain.services.MetricQueryService;
import com.upc.courtly.analytics.infrastructure.persistence.jpa.repositories.MetricRepository;
import com.upc.courtly.coaches.infrastructure.persistence.jpa.repositories.CoachRepository;
import com.upc.courtly.payments.domain.model.valueobjects.PaymentContextType;
import com.upc.courtly.payments.infrastructure.persistence.jpa.repositories.PaymentRepository;
import com.upc.courtly.reviews.domain.model.valueobjects.ReviewTargetType;
import com.upc.courtly.reviews.infrastructure.persistence.jpa.repositories.ReviewRepository;
import com.upc.courtly.trainingsessions.domain.model.valueobjects.TrainingSessionStatus;
import com.upc.courtly.trainingsessions.infrastructure.persistence.jpa.repositories.TrainingSessionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MetricQueryServiceImpl implements MetricQueryService {
    private final MetricRepository metricRepository;
    private final CoachRepository coachRepository;
    private final TrainingSessionRepository trainingSessionRepository;
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;

    public MetricQueryServiceImpl(MetricRepository metricRepository,
                                  CoachRepository coachRepository,
                                  TrainingSessionRepository trainingSessionRepository,
                                  PaymentRepository paymentRepository,
                                  ReviewRepository reviewRepository) {
        this.metricRepository = metricRepository;
        this.coachRepository = coachRepository;
        this.trainingSessionRepository = trainingSessionRepository;
        this.paymentRepository = paymentRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public List<Metric> handle(GetAllMetricsQuery query) {
        return metricRepository.findAll();
    }

    @Override
    public Optional<Metric> handle(GetMetricByIdQuery query) {
        return metricRepository.findById(query.metricId());
    }

    @Override
    public List<Metric> handleCoachMetrics(Long coachId) {
        var coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new IllegalArgumentException("Coach with id " + coachId + " not found"));

        var trainingSessions = trainingSessionRepository.findAll().stream()
                .filter(session -> session.getCoach().getId().equals(coachId))
                .toList();

        var completedSessions = trainingSessions.stream()
                .filter(session -> session.getStatus() == TrainingSessionStatus.COMPLETED)
                .count();

        var receivedRequests = trainingSessions.size();

        var revenue = paymentRepository.findAll().stream()
                .filter(payment -> payment.getContextType() == PaymentContextType.TRAINING_SESSION)
                .filter(payment -> payment.getTrainingSession() != null)
                .filter(payment -> payment.getTrainingSession().getCoach().getId().equals(coachId))
                .map(payment -> payment.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var coachReviews = reviewRepository.findAll().stream()
                .filter(review -> review.getTargetType() == ReviewTargetType.COACH)
                .filter(review -> review.getTargetId().equals(coachId))
                .toList();

        BigDecimal averageRating = BigDecimal.ZERO;
        if (!coachReviews.isEmpty()) {
            averageRating = BigDecimal.valueOf(
                    coachReviews.stream().mapToInt(review -> review.getScore()).average().orElse(0.0)
            ).setScale(2, RoundingMode.HALF_UP);
        }

        var now = LocalDateTime.now();
        var metrics = new ArrayList<Metric>();
        metrics.add(buildMetric(1L, MetricType.SESSIONS_COMPLETED, BigDecimal.valueOf(completedSessions), "ALL_TIME", coach, now));
        metrics.add(buildMetric(2L, MetricType.BOOKINGS_RECEIVED, BigDecimal.valueOf(receivedRequests), "ALL_TIME", coach, now));
        metrics.add(buildMetric(3L, MetricType.REVENUE_TOTAL, revenue.setScale(2, RoundingMode.HALF_UP), "ALL_TIME", coach, now));
        metrics.add(buildMetric(4L, MetricType.AVERAGE_RATING, averageRating, "ALL_TIME", coach, now));
        return metrics;
    }

    @Override
    public Optional<Metric> handleCoachMetric(Long coachId, Long metricId) {
        return handleCoachMetrics(coachId).stream()
                .filter(metric -> metric.getId().equals(metricId))
                .findFirst();
    }

    private Metric buildMetric(Long syntheticId, MetricType metricType, BigDecimal value, String period,
                               com.upc.courtly.coaches.domain.model.aggregates.Coach coach, LocalDateTime createdAt) {
        var metric = new Metric(metricType, value, period, coach);
        metric.setId(syntheticId);
        metric.setCreatedAt(createdAt);
        return metric;
    }
}
