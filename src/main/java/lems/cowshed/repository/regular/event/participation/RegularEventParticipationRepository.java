package lems.cowshed.repository.regular.event.participation;

import lems.cowshed.domain.regular.event.participation.RegularEventParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RegularEventParticipationRepository extends JpaRepository<RegularEventParticipation, Long> {

    @Query("select COUNT(rep) from RegularEventParticipation rep where rep.regularEvent.id = :regularId")
    long getParticipantCountByRegularId(@Param("regularId") Long regularId);

    Optional<RegularEventParticipation> findByIdAndUserId(Long id, Long userId);

    Optional<RegularEventParticipation> findByRegularEventIdAndUserId(Long regularEventId, Long userId);
}
