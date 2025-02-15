package lems.cowshed.domain.event;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;


public interface EventRepository extends JpaRepository<Event, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Event> findPessimisticLockById(Long eventId);
    Slice<Event> findSliceBy(Pageable pageable);
    Event findByName(String name);
}
