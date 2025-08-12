package lems.cowshed.repository.event.participation;

import lems.cowshed.domain.event.participation.EventParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventParticipantRepository extends JpaRepository<EventParticipation, Long> {

    @Query("SELECT COUNT(ep) From EventParticipation ep Where ep.eventId = :eventId")
    long getParticipationCountById(@Param("eventId") Long eventId);

    List<EventParticipation> findEventParticipationByEventIdIn(List<Long> eventIds);

    Optional<EventParticipation> findByEventIdAndUserId(Long eventId, Long userId);

    @Query("select ep from EventParticipation ep where ep.eventId in :eventId")
    List<EventParticipation> findByEventId(@Param("eventId") Long eventId);

    @Query("select ep from EventParticipation ep where ep.eventId in :eventIds")
    List<EventParticipation> findByEventIdIn(@Param("eventIds") List<Long> eventIds);

    @Query("SELECT count(ep) from EventParticipation ep where ep.user.id = :userId")
    long findCountByUserId(@Param("userId") Long userId);
}