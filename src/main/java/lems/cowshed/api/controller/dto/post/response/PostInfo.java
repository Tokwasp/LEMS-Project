package lems.cowshed.api.controller.dto.post.response;

import lems.cowshed.domain.post.Post;
import lems.cowshed.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
public class PostInfo {

    private String subject;
    private String content;
    private LocalDateTime createdDateTime;

    private String username;
    private boolean isRegistrant;
    private long commentCount;

    @Builder
    private PostInfo(String subject, String content, LocalDateTime createdDateTime,
                     String username, boolean isRegistrant, long commentCount) {
        this.subject = subject;
        this.content = content;
        this.createdDateTime = createdDateTime;
        this.username = username;
        this.isRegistrant = isRegistrant;
        this.commentCount = commentCount;
    }

    public static PostInfo of(Post post, String username, Long userId, int commentCount){
        return PostInfo.builder()
                .subject(post.getSubject())
                .content(post.getContent())
                .username(username)
                .createdDateTime(post.getCreatedDateTime())
                .isRegistrant(post.isSameUserId(userId))
                .commentCount(commentCount)
                .build();
    }

    public static List<PostInfo> convertToPostInfo(List<Post> pagingPosts, Map<Long, User> userIdMap, Long userId) {
        return pagingPosts.stream()
                .map(post -> PostInfo.of(
                        post,
                        userIdMap.get(post.getUserId()).getUsername(),
                        userId,
                        post.getComments().size()
                ))
                .toList();
    }
}
