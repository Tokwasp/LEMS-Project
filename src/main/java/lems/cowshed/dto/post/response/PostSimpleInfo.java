package lems.cowshed.dto.post.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.post.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostSimpleInfo {

    @Schema(description = "제목")
    private String subject;

    @Schema(description = "내용")
    private String content;

    @Builder
    private PostSimpleInfo(String subject, String content) {
        this.subject = subject;
        this.content = content;
    }

    public static PostSimpleInfo from(Post post) {
        return PostSimpleInfo.builder()
                .subject(post.getSubject())
                .content(post.getContent())
                .build();
    }
}
