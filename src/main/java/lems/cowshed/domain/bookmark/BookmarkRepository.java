package lems.cowshed.domain.bookmark;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    @Query("select b from Bookmark b join b.user u where u.id = :userId")
    List<Bookmark> findByUserId(@Param("userId") Long userId);

    Slice<Bookmark> findSliceByUserId(Pageable pageable, Long userId);

    @Query("select b from Bookmark b where b.user.id = :userId And b.event.id = :eventId And b.status = :bookmarkStatus")
    Optional<Bookmark> findBookmark(Long userId, Long eventId, BookmarkStatus bookmarkStatus);
}
