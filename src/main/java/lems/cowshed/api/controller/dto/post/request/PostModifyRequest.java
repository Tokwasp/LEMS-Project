package lems.cowshed.api.controller.dto.post.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostModifyRequest {

    @NotBlank
    private String subject;
    @NotBlank
    private String content;

    @Builder
    private PostModifyRequest(String subject, String content) {
        this.subject = subject;
        this.content = content;
    }
}
