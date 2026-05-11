package com.upc.courtly.availabilities.infrastructure.persistence.jpa.repositories;

import com.upc.courtly.availabilities.domain.model.aggregates.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByCoachUserIdOrderByDateAscStartTimeAsc(Long userId);

    @Query("""
            select count(a) > 0
            from Availability a
            where a.coach.id = :coachId
              and a.date = :date
              and (:excludeId is null or a.id <> :excludeId)
              and a.startTime < :endTime
              and a.endTime > :startTime
            """)
    boolean existsOverlappingAvailability(@Param("coachId") Long coachId,
                                          @Param("date") LocalDate date,
                                          @Param("startTime") LocalTime startTime,
                                          @Param("endTime") LocalTime endTime,
                                          @Param("excludeId") Long excludeId);
}
