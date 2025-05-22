package lems.cowshed.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByIdAndUserId(Long postId, Long userId);

    @Query("select distinct p from Post p join fetch Comment c on p.id = c.post.id where p.id in :postIds")
    List<Post> findPostFetchComment(@Param("postIds") List<Long> postIds);
}