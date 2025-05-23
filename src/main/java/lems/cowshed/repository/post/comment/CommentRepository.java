package lems.cowshed.repository.post.comment;

import lems.cowshed.domain.post.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdAndUserId(Long commentId, Long userId);
    List<Comment> findByPostId(Long postId);
}
