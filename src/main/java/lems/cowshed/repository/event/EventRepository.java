package lems.cowshed.repository.event;

import jakarta.persistence.LockModeType;
import lems.cowshed.domain.bookmark.BookmarkStatus;
import lems.cowshed.domain.event.Event;
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
    Optional<Event> findPessimisticById(Long eventId);

    Slice<Event> findEventsBy(Pageable pageable);
    Event findByName(String name);

    @Query("select distinct e from Event e left join fetch e.participants where e.id = :eventId and e.author = :author")
    Optional<Event> findByIdAndAuthorFetchParticipation(@Param("eventId") Long eventId, @Param("author") String author);

    Optional<Event> findByIdAndAuthor(Long eventId, String author);

    @Query("SELECT b.event.id FROM Bookmark b WHERE b.userId = :userId AND b.event.id IN :eventIds AND b.status = :bookmarkStatus")
    Set<Long> findEventIdsBookmarkedByUser(@Param("userId") Long userId, @Param("eventIds") List<Long> eventIds, @Param("bookmarkStatus") BookmarkStatus bookmarkStatus);

    @Query("select distinct e from Event e left join fetch e.participants ep where e.id In :eventIds order by e.id desc")
    List<Event> findByIdInFetchParticipation(List<Long> eventIds);

    @Query("select distinct e from Event e left join fetch e.bookmarks b where e.id In :eventIds")
    List<Event> findByIdInFetchBookmarks(List<Long> eventIds);
}