package lems.cowshed.dto.comment.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentInfo {

    private Long id;
    private String username;
    private String daysAgo;
    private String content;
    private boolean isRegistrant;

    @Builder
    private CommentInfo(Long id, String username, String daysAgo, String content, boolean isRegistrant) {
        this.id = id;
        this.username = username;
        this.daysAgo = daysAgo;
        this.content = content;
        this.isRegistrant = isRegistrant;
    }

    public static CommentInfo of(Long id, String username, String daysAgo, String content, boolean isRegistrant){
        return CommentInfo.builder()
                .id(id)
                .username(username)
                .daysAgo(daysAgo)
                .content(content)
                .isRegistrant(isRegistrant)
                .build();
    }
}
