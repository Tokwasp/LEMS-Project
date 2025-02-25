package lems.cowshed.domain.userevent;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserEventRepository extends JpaRepository<UserEvent, Long> {

    @Query("SELECT COUNT(ue) From UserEvent ue Where ue.event.id = :eventId")
    long countParticipantByEventId(@Param("eventId") Long eventId);

    @EntityGraph(attributePaths = {"user"})
    List<UserEvent> findFetchUserByEventId (Long eventId);
    List<UserEvent> findByEventIdIn (List<Long> eventIds);
    Optional<UserEvent> findByEventIdAndUserId (Long EventId, Long userId);
}