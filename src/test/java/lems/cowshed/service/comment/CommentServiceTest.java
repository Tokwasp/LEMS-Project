package lems.cowshed.service.comment;

import jakarta.persistence.EntityManager;
import lems.cowshed.IntegrationTestSupport;
import lems.cowshed.api.controller.dto.comment.request.CommentModifyRequest;
import lems.cowshed.api.controller.dto.comment.request.CommentSaveRequest;
import lems.cowshed.api.controller.dto.comment.response.CommentInfo;
import lems.cowshed.api.controller.dto.comment.response.CommentsInfo;
import lems.cowshed.domain.comment.Comment;
import lems.cowshed.domain.post.Post;
import lems.cowshed.domain.post.PostRepository;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

class CommentServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private EntityManager em;

    @DisplayName("게시글의 댓글들을 조회 한다.")
    @Test
    void getPostComments() {
        //given
        User user = createUser("테스터", "test@naver.com");
        userRepository.save(user);

        Post post = createPost("제목", "내용", user.getId());
        Comment comment = createComment("댓글", user.getId());
        comment.connectPost(post);
        postRepository.save(post);

        User user2 = createUser("테스터2", "test2@naver.com");
        userRepository.save(user2);

        Comment comment2 = createComment("댓글2", user2.getId());
        comment2.connectPost(post);
        postRepository.save(post);

        //when
        LocalDateTime currentDateTime = LocalDateTime.of(2025, 5, 22, 22, 0);
        CommentsInfo commentsInfo = commentService.getPostComments(post.getId(), user.getId(), currentDateTime);

        //then
        List<CommentInfo> comments = commentsInfo.getComments();
        assertThat(comments).hasSize(2)
                .extracting("username", "isRegistrant", "content")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("테스터", true, "댓글"),
                        Tuple.tuple("테스터2", false, "댓글2")
                );
    }

    @DisplayName("댓글을 저장 한다")
    @Test
    void save() {
        //given
        long userId = 1L;
        Post post = createPost("제목", "내용", userId);
        postRepository.save(post);

        CommentSaveRequest request = createCommentRequest("댓글");

        //when
        Long savedCommentId = commentService.save(request, post.getId(), userId);

        //then
        Comment comment = commentRepository.findById(savedCommentId).orElseThrow();
        assertThat(comment.getContext()).isEqualTo("댓글");

        Post findPost = comment.getPost();
        assertThat(findPost.getSubject()).isEqualTo("제목");
        assertThat(findPost.getContent()).isEqualTo("내용");
    }

    @DisplayName("댓글을 수정 한다")
    @Test
    void modify() {
        //given
        long userId = 1L;

        Post post = createPost("제목", "내용", userId);
        Comment comment = createComment("댓글", userId);
        comment.connectPost(post);
        postRepository.save(post);

        CommentModifyRequest request = createModifyRequest("변경된 댓글");

        //when
        Long modifiedCommentId = commentService.modify(request, comment.getId(), userId);
        em.flush(); em.clear();

        //then
        Comment findComment = commentRepository.findById(modifiedCommentId).orElseThrow();
        assertThat(findComment.getContext()).isEqualTo("변경된 댓글");

        Post findPost = findComment.getPost();
        assertThat(findPost.getSubject()).isEqualTo("제목");
        assertThat(findPost.getContent()).isEqualTo("내용");
    }

    // 영속성 전이 문제
    @Disabled
    @DisplayName("댓글을 삭제 한다.")
    @Test
    void delete() {
        //given
        long userId = 1L;

        Post post = createPost("제목", "내용", userId);
        Comment comment = createComment("댓글", userId);
        comment.connectPost(post);
        postRepository.save(post);

        //when
        Long deletedCommentId = commentService.delete(comment.getId(), userId);
        em.flush(); em.clear();

        //then
        assertThatThrownBy(() -> commentRepository.findById(deletedCommentId).orElseThrow())
                .isInstanceOf(NoSuchElementException.class);
    }

    private CommentSaveRequest createCommentRequest(String context){
        return CommentSaveRequest.builder()
                .context(context)
                .build();
    }

    private CommentModifyRequest createModifyRequest(String context){
        return CommentModifyRequest.builder()
                .context(context)
                .build();
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

    private User createUser(String username, String email) {
        return User.builder()
                .username(username)
                .email(email)
                .build();
    }

}