package lems.cowshed.domain.post;

import lems.cowshed.IntegrationTestSupport;
import lems.cowshed.domain.comment.Comment;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventRepository;
import lems.cowshed.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


class PostRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PostRepository postRepository;

    @DisplayName("회원 id와 게시글 id를 통해 게시글을 조회 한다.")
    @Test
    void findByIdAndUserId() {
        //given
        Event event = createEvent("테스터", "모임 이름");
        eventRepository.save(event);

        Long userId = 1L;
        Post post = createPost(event, "제목", "내용", userId);
        postRepository.save(post);

        //when
        Post findPost = postRepository.findByIdAndUserId(post.getId(), userId).orElseThrow();

        //then
        assertThat(findPost.getUserId()).isEqualTo(userId);
        assertThat(findPost.getSubject()).isEqualTo("제목");
        assertThat(findPost.getContent()).isEqualTo("내용");

        Event findEvent = post.getEvent();
        assertThat(findEvent.getAuthor()).isEqualTo("테스터");
        assertThat(findEvent.getName()).isEqualTo("모임 이름");
    }

    @DisplayName("게시글과 댓글을 함께 조회 한다.")
    @Test
    void findPostFetchComment() {
        //given
        long userId = 1L;
        Post post = createPost(null, "제목", "내용", userId);

        createComment(post, "댓글", userId);
        createComment(post,"댓글2", userId);
        createComment(post,"댓글3", userId);
        postRepository.save(post);

        //when
        Post findPost = postRepository.findByIdAndUserId(post.getId(), userId).orElseThrow();

        //then
        assertThat(findPost.getSubject()).isEqualTo("제목");
        assertThat(findPost.getContent()).isEqualTo("내용");

        List<Comment> comments = findPost.getComments();
        assertThat(comments).hasSize(3)
                .extracting("context")
                .containsExactlyInAnyOrder("댓글", "댓글2", "댓글3");
    }

    private static Event createEvent(String author, String name) {
        return Event.builder()
                .name(name)
                .author(author)
                .build();
    }

    private Comment createComment(Post post, String context, Long userId){
        Comment comment = Comment.builder()
                .userId(userId)
                .context(context)
                .build();

        comment.connectPost(post);
        return comment;
    }

    private Post createPost(Event event, String subject, String content, Long userId){
        return Post.builder()
                .event(event)
                .subject(subject)
                .content(content)
                .userId(userId)
                .build();
    }
}