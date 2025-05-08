package lems.cowshed.domain.regular.event.participation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RegularEventParticipationRepository extends JpaRepository<RegularEventParticipation, Long> {

    @Modifying(clearAutomatically = true)
    @Query("delete from RegularEventParticipation rep where rep.id = :participationId And rep.userId = :userId")
    int deleteByIdAndUserId(@Param("participationId") Long participationId, @Param("userId") Long userId);
}
