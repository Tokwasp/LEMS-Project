package lems.cowshed.domain.event.participation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {

    @Query("SELECT COUNT(ep) From EventParticipant ep Where ep.event.id = :eventId")
    long countParticipantByEventId(@Param("eventId") Long eventId);

    List<EventParticipant> findEventParticipationByEventIdIn(List<Long> eventIds);
    Optional<EventParticipant> findByEventIdAndUserId (Long EventId, Long userId);
}