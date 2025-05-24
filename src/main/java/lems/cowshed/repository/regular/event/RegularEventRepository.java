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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RegularEvent> findWithLockById(Long regularId);

    @Query("select count(rep) from RegularEventParticipation rep join rep.regularEvent re where re.id = :regularId")
    long getParticipantCount(@Param("regularId") long regularId);

    @Query("select rep.userId from RegularEventParticipation rep join rep.regularEvent re where re.id = :regularId")
    List<Long> findParticipantsUserIdsByRegularId(@Param("regularId") Long regularId);

    @Query("select re from RegularEvent re left join fetch re.participations rep where re.id = :regularId")
    RegularEvent findByIdFetchParticipation(@Param("regularId") Long regularId);

    @Query("select distinct re from RegularEvent re left join fetch re.participations rep where re.id in :regularIds")
    List<RegularEvent> findByIdsFetchParticipation(@Param("regularIds") List<Long> regularEventIds);

    Slice<RegularEvent> findByEventId(Long eventId, Pageable pageable);
}
