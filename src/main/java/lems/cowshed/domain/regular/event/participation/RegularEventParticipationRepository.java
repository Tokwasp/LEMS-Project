package lems.cowshed.domain.regular.event.participation;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RegularEventParticipationRepository extends JpaRepository<RegularEventParticipation, Long> {
}
