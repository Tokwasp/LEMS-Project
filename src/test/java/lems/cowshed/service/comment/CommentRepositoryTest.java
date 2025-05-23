package lems.cowshed.service.comment;

import lems.cowshed.IntegrationTestSupport;
import lems.cowshed.domain.post.comment.Comment;
import lems.cowshed.domain.post.Post;
import lems.cowshed.repository.post.comment.CommentRepository;
import lems.cowshed.repository.post.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class CommentRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @DisplayName("댓글 id와 회원 id를 통해 댓글을 조회 한다.")
    @Test
    void findByIdAndUserId() {
        //given
        long userId = 1L;
        Post post = createPost("제목", "내용", userId);
        Comment comment = createComment("댓글", userId);
        comment.connectPost(post);
        postRepository.save(post);

        //when
        Post findPost = postRepository.findByIdAndUserId(post.getId(), userId).orElseThrow();

        //then
        assertThat(findPost.getSubject()).isEqualTo("제목");
        assertThat(findPost.getContent()).isEqualTo("내용");

        Comment findComment = findPost.getComments().get(0);
        assertThat(findComment.getContext()).isEqualTo("댓글");
    }

    @DisplayName("게시글 id를 통해 댓글들을 찾는다.")
    @Test
    void findByPostId() {
        //given
        long userId = 1L;
        Post post = createPost("제목", "내용", userId);

        Comment comment = createComment("댓글", userId);
        comment.connectPost(post);

        Comment comment2 = createComment("댓글2", userId);
        comment2.connectPost(post);

        postRepository.save(post);

        //when
        List<Comment> comments = commentRepository.findByPostId(post.getId());

        //then
        assertThat(comments).hasSize(2)
                .extracting("context")
                .containsExactlyInAnyOrder("댓글", "댓글2");
    }

    private Comment createComment(String context, Long userId){
        return Comment.builder()
                .userId(userId)
                .context(context)
                .build();
    }

    private Post createPost(String subject, String content, Long userId){
        return Post.builder()
                .subject(subject)
                .content(content)
                .userId(userId)
                .build();
    }
}