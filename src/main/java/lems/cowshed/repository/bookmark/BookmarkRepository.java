package lems.cowshed.repository.bookmark;

import lems.cowshed.domain.bookmark.Bookmark;
import lems.cowshed.domain.bookmark.BookmarkStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    List<Bookmark> findByUserId(Long userId);

    @Query("select b from Bookmark b where b.userId = :userId And b.event.id = :eventId And b.status = :bookmarkStatus")
    Optional<Bookmark> findBookmark(@Param("userId") Long userId, @Param("eventId") Long eventId, @Param("bookmarkStatus") BookmarkStatus bookmarkStatus);

    @Query("select count(b) from Bookmark b where b.userId = :userId")
    long findCountByUserId(@Param("userId") Long userId);
}
