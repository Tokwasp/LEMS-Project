package lems.cowshed.repository.event.participation;

import lems.cowshed.domain.event.participation.EventParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventParticipantRepository extends JpaRepository<EventParticipation, Long> {

    @Query("SELECT COUNT(ep) From EventParticipation ep Where ep.event.id = :eventId")
    long getParticipationCountById(@Param("eventId") Long eventId);

    List<EventParticipation> findEventParticipationByEventIdIn(List<Long> eventIds);
    Optional<EventParticipation> findByEventIdAndUserId (Long eventId, Long userId);
}