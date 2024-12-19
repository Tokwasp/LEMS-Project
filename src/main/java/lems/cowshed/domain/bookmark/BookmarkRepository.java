package lems.cowshed.domain.bookmark;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    @Query("select b from Bookmark b join b.user u where u.id = :userId")
    List<Bookmark> findByUserId(@Param("userId") Long userId);
}
