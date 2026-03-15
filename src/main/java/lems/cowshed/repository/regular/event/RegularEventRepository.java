package lems.cowshed.repository.regular.event;

import lems.cowshed.domain.regular.event.RegularEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RegularEventRepository extends JpaRepository<RegularEvent, Long> {

    @Modifying
    @Query("UPDATE RegularEvent r SET r.participantCount = r.participantCount + 1 WHERE r.id = :id")
    int increaseParticipantCount(@Param("id") Long id);

    @Query("select rep.userId from RegularEventParticipation rep join RegularEvent re on rep.regularEventId = re.id where re.id = :regularId")
    List<Long> findParticipantsUserIdsByRegularId(@Param("regularId") Long regularId);

    Slice<RegularEvent> findByEventId(Long eventId, Pageable pageable);

    List<RegularEvent> findByEventId(long eventId);
}
