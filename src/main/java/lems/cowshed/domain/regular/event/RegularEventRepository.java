package lems.cowshed.domain.regular.event;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface RegularEventRepository extends JpaRepository<RegularEvent, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RegularEvent> findEventWithLockById(Long regularId);
}
