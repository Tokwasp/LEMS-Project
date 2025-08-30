package lems.cowshed.repository.regular.event;

import jakarta.persistence.LockModeType;
import lems.cowshed.domain.regular.event.RegularEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RegularEventRepository extends JpaRepository<RegularEvent, Long> {

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    Optional<RegularEvent> findWithOptimisticLockById(Long regularId);

    @Query("select count(rep) from RegularEventParticipation rep join RegularEvent re on rep.regularEventId = re.id where re.id = :regularId")
    long getParticipantCount(@Param("regularId") long regularId);

    @Query("select rep.userId from RegularEventParticipation rep join RegularEvent re on rep.regularEventId = re.id where re.id = :regularId")
    List<Long> findParticipantsUserIdsByRegularId(@Param("regularId") Long regularId);

    Slice<RegularEvent> findByEventId(Long eventId, Pageable pageable);

    List<RegularEvent> findByEventId(long eventId);
}
