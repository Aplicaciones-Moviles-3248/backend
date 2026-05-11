package com.upc.courtly.bookings.infrastructure.persistence.jpa.repositories;

import com.upc.courtly.bookings.domain.model.aggregates.Booking;
import com.upc.courtly.bookings.domain.model.valueobjects.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("""
            select count(b) > 0
            from Booking b
            where b.court.id = :courtId
              and b.status in :statuses
              and b.startTime < :endTime
              and b.endTime > :startTime
            """)
    boolean existsOverlappingBooking(@Param("courtId") Long courtId,
                                     @Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime,
                                     @Param("statuses") Collection<BookingStatus> statuses);
}

