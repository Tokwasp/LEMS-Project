package lems.cowshed.api.controller.dto.post.request;

import jakarta.validation.constraints.NotBlank;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.post.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostSaveRequest {

    @NotBlank
    private String subject;
    @NotBlank
    private String content;

    @Builder
    private PostSaveRequest(String subject, String content) {
        this.subject = subject;
        this.content = content;
    }

    public Post toEntity(Event event, Long userId){
        return Post.builder()
                .event(event)
                .userId(userId)
                .subject(this.subject)
                .content(this.content)
                .build();
    }
}
