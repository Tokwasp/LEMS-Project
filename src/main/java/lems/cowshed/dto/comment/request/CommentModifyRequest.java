package lems.cowshed.dto.comment.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentModifyRequest {

    @NotBlank
    private String context;

    @Builder
    private CommentModifyRequest(String context) {
        this.context = context;
    }
}
