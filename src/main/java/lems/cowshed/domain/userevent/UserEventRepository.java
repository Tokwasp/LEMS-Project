package lems.cowshed.domain.userevent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserEventRepository extends JpaRepository<UserEvent, Long> {

    @Query("SELECT COUNT(ue) From UserEvent ue Where ue.event.id = :eventId")
    long countParticipantByEventId(@Param("eventId") Long eventId);
}