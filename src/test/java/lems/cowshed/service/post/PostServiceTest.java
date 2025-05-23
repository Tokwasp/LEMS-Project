package lems.cowshed.service.post;

import jakarta.persistence.EntityManager;
import lems.cowshed.IntegrationTestSupport;
import lems.cowshed.dto.post.request.PostModifyRequest;
import lems.cowshed.dto.post.request.PostSaveRequest;
import lems.cowshed.dto.post.response.PostInfo;
import lems.cowshed.dto.post.response.PostPagingInfo;
import lems.cowshed.domain.post.comment.Comment;
import lems.cowshed.domain.event.Event;
import lems.cowshed.repository.event.EventRepository;
import lems.cowshed.domain.post.Post;
import lems.cowshed.repository.post.PostRepository;
import lems.cowshed.domain.user.User;
import lems.cowshed.repository.user.UserRepository;
import lems.cowshed.repository.post.comment.CommentRepository;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class PostServiceTest extends IntegrationTestSupport {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private EntityManager em;

    @DisplayName("게시글을 저장 한다.")
    @Test
    void save() {
        //given
        User user = createUser("테스터", "test@naver.com");
        userRepository.save(user);

        Event event = createEvent("테스터");
        eventRepository.save(event);

        PostSaveRequest request = createPostRequest("제목", "내용");

        //when
        Long savedPostId = postService.save(request, event.getId(), user.getId());

        //then
        Post post = postRepository.findById(savedPostId).orElseThrow();
        assertThat(post.getSubject()).isEqualTo("제목");
        assertThat(post.getContent()).isEqualTo("내용");
        assertThat(post.getEvent().getAuthor()).isEqualTo("테스터");
        assertThat(post.getUserId()).isEqualTo(user.getId());
    }

    @DisplayName("게시글을 수정 한다.")
    @Test
    void modify() {
        //given
        Event event = createEvent("테스터");
        eventRepository.save(event);

        Post post = createPost("제목", "내용", event, 1L);
        postRepository.save(post);

        PostModifyRequest request = createPostModifyRequest("수정된 제목", "수정된 내용");

        //when
        Long modifiedPostId = postService.modify(request, 1L, post.getId());
        em.flush(); em.clear();

        //then
        Post findPost = postRepository.findById(modifiedPostId).orElseThrow();
        assertThat(findPost.getSubject()).isEqualTo("수정된 제목");
        assertThat(findPost.getContent()).isEqualTo("수정된 내용");
        assertThat(findPost.getEvent().getAuthor()).isEqualTo("테스터");
        assertThat(findPost.getUserId()).isEqualTo(1L);
    }

    @DisplayName("게시글을 삭제 한다.")
    @Test
    void delete() {
        //given
        Event event = createEvent("테스터");
        eventRepository.save(event);

        Post post = createPost("제목", "내용", event, 1L);
        postRepository.save(post);

        //when
        Long deletedPostId = postService.delete(post.getId(), 1L);
        em.flush();

        //then
        assertThat(postRepository.findById(deletedPostId)).isEmpty();
    }

    @DisplayName("게시글을 페이징 조회 한다.")
    @Test
    void getPaging() {
        //given
        User user = createUser("테스터", "test@naver.com");
        userRepository.save(user);

        Event event = createEvent("테스터");
        eventRepository.save(event);

        Post post = createPost("제목", "내용", event, user.getId());

        createComment(post,"댓글1", user.getId());
        createComment(post,"댓글2", user.getId());
        createComment(post,"댓글3", user.getId());
        postRepository.save(post);

        //when
        PageRequest pageRequest = PageRequest.of(0, 1);
        PostPagingInfo result = postService.getPaging(pageRequest, user.getId());

        //then
        List<PostInfo> postInfos = result.getPostInfos();
        assertThat(postInfos).hasSize(1);
        assertThat(postInfos)
                .extracting("subject", "content", "username", "isRegistrant", "commentCount")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("제목", "내용","테스터", true, 3L)
                );
    }

    private Comment createComment(Post post, String context, Long userId) {
        Comment comment = Comment.builder()
                .context(context)
                .userId(userId)
                .build();

        comment.connectPost(post);
        return comment;
    }

    private PostModifyRequest createPostModifyRequest(String subject, String content){
        return PostModifyRequest.builder()
                .subject(subject)
                .content(content)
                .build();
    }

    private PostSaveRequest createPostRequest(String subject, String content){
        return PostSaveRequest.builder()
                .subject(subject)
                .content(content)
                .build();
    }

    private static Event createEvent(String author) {
        return Event.builder()
                .name("자전거 모임")
                .author(author)
                .build();
    }

    private User createUser(String username, String mail) {
        return User.builder()
                .username(username)
                .email(mail)
                .build();
    }

    private Post createPost(String subject, String content, Event event, Long userId){
        return Post.builder()
                .event(event)
                .subject(subject)
                .content(content)
                .userId(userId)
                .build();
    }
}