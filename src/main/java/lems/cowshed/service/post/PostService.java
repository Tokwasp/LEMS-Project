package lems.cowshed.service.post;

import lems.cowshed.api.controller.dto.post.request.PostModifyRequest;
import lems.cowshed.api.controller.dto.post.request.PostSaveRequest;
import lems.cowshed.api.controller.dto.post.response.PostInfo;
import lems.cowshed.api.controller.dto.post.response.PostPagingInfo;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventRepository;
import lems.cowshed.domain.post.Post;
import lems.cowshed.domain.post.PostRepository;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.exception.Message;
import lems.cowshed.exception.NotFoundException;
import lems.cowshed.exception.Reason;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final PostRepository postRepository;

    @Transactional
    public Long save(PostSaveRequest request, Long eventId, Long userId){
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(Reason.EVENT_ID, Message.EVENT_NOT_FOUND));

        return postRepository.save(request.toEntity(event, userId)).getId();
    }

    @Transactional
    public Long modify(PostModifyRequest request, Long userId, Long postId) {
        Post post = postRepository.findByIdAndUserId(postId, userId)
                .orElseThrow(() -> new NotFoundException(Reason.POST_ID, Message.POST_ID));

        post.modify(
                request.getSubject(),
                request.getContent()
        );

        return post.getId();
    }

    @Transactional
    public Long delete(Long postId, Long userId) {
        Post post = postRepository.findByIdAndUserId(postId, userId)
                .orElseThrow(() -> new NotFoundException(Reason.POST_ID, Message.POST_ID));

        postRepository.delete(post);
        return post.getId();
    }

    public PostPagingInfo getPaging(Pageable pageable, Long userId) {
        Slice<Post> pagingPosts = postRepository.findAll(pageable);
        List<Long> userIds = getUserIdsFrom(pagingPosts.getContent());

        List<User> users = userRepository.findByIdIn(userIds);
        Map<Long, User> userIdMap = groupUserIdMapFrom(users);

        List<Long> postIds = getPostIds(pagingPosts);
        List<Post> posts = postRepository.findPostFetchComment(postIds);

        List<PostInfo> postInfos = PostInfo.convertToPostInfo(posts, userIdMap, userId);
        return PostPagingInfo.of(postInfos, pagingPosts.hasNext());
    }

    private List<Long> getPostIds(Slice<Post> pagingPosts) {
        return pagingPosts.stream()
                .map(Post::getId)
                .toList();
    }

    private List<Long> getUserIdsFrom(List<Post> content) {
        return content.stream()
                .map(Post::getUserId)
                .toList();
    }

    private Map<Long, User> groupUserIdMapFrom(List<User> users) {
        return users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }
}
