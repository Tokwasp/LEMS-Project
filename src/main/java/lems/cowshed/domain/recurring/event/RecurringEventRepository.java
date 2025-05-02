package lems.cowshed.domain.recurring.event;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecurringEventRepository extends JpaRepository<RecurringEvent, Long> {
}
