package com.upc.courtly.trainingsessions.infrastructure.persistence.jpa.repositories;

import com.upc.courtly.trainingsessions.domain.model.aggregates.TrainingSession;
import com.upc.courtly.trainingsessions.domain.model.valueobjects.TrainingSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {
    @Query("""
            select count(ts) > 0
            from TrainingSession ts
            where ts.court.id = :courtId
              and ts.status in :statuses
              and ts.startTime < :endTime
              and ts.endTime > :startTime
            """)
    boolean existsOverlappingCourtAssignment(@Param("courtId") Long courtId,
                                             @Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime,
                                             @Param("statuses") Collection<TrainingSessionStatus> statuses);
}
