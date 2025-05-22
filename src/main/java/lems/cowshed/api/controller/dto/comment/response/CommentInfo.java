package lems.cowshed.api.controller.dto.comment.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentInfo {

    private String username;
    private String daysAgo;
    private String content;

    @Builder
    private CommentInfo(String username, String daysAgo, String content) {
        this.username = username;
        this.daysAgo = daysAgo;
        this.content = content;
    }

    public static CommentInfo of(String username, String daysAgo, String content){
        return CommentInfo.builder()
                .username(username)
                .daysAgo(daysAgo)
                .content(content)
                .build();
    }
}
