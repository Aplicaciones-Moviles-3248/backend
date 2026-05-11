package com.upc.courtly.payments.infrastructure.persistence.jpa.repositories;

import com.upc.courtly.payments.domain.model.aggregates.Payment;
import com.upc.courtly.payments.domain.model.valueobjects.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserIdOrderByCreatedAtDesc(Long userId);
    boolean existsByBookingIdAndPaymentStatus(Long bookingId, PaymentStatus paymentStatus);
    boolean existsByTrainingSessionIdAndPaymentStatus(Long trainingSessionId, PaymentStatus paymentStatus);
}

