package lems.cowshed.repository.regular.event.participation;

import lems.cowshed.domain.regular.event.participation.RegularEventParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RegularEventParticipationRepository extends JpaRepository<RegularEventParticipation, Long> {

    @Query("select COUNT(rep) from RegularEventParticipation rep where rep.regularEventId = :regularId")
    long getParticipantCountByRegularId(@Param("regularId") Long regularId);

    Optional<RegularEventParticipation> findByIdAndUserId(Long id, Long userId);

    Optional<RegularEventParticipation> findByRegularEventIdAndUserId(Long regularEventId, Long userId);

    List<RegularEventParticipation> findByRegularEventId(long regularEventId);

    @Query("select rep from RegularEventParticipation rep where rep.regularEventId in :regularEventIds")
    List<RegularEventParticipation> findByRegularEventIdIn(@Param("regularEventIds") List<Long> regularEventIds);
}
