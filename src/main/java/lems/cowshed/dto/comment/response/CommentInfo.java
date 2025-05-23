package lems.cowshed.dto.comment.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentInfo {

    private String username;
    private String daysAgo;
    private String content;
    private boolean isRegistrant;

    @Builder
    private CommentInfo(String username, String daysAgo, String content, boolean isRegistrant) {
        this.username = username;
        this.daysAgo = daysAgo;
        this.content = content;
        this.isRegistrant = isRegistrant;
    }

    public static CommentInfo of(String username, String daysAgo, String content, boolean isRegistrant){
        return CommentInfo.builder()
                .username(username)
                .daysAgo(daysAgo)
                .content(content)
                .isRegistrant(isRegistrant)
                .build();
    }
}
