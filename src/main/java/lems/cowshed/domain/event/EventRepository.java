package lems.cowshed.domain.event;

import jakarta.persistence.LockModeType;
import lems.cowshed.domain.bookmark.BookmarkStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface EventRepository extends JpaRepository<Event, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Event> findPessimisticLockById(Long eventId);
    Slice<Event> findSliceBy(Pageable pageable);
    Event findByName(String name);
    Optional<Event> findByIdAndAuthor(Long eventId, String author);
    @Query("SELECT b.event.id FROM Bookmark b WHERE b.user.id = :userId AND b.event.id IN :eventIds AND b.status = :bookmarkStatus")
    Set<Long> findBookmarkedEventsFromMe(@Param("userId") Long userId, @Param("eventIds") List<Long> eventIds, @Param("bookmarkStatus") BookmarkStatus bookmarkStatus);
}