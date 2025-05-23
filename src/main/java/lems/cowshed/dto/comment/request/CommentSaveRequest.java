package lems.cowshed.dto.comment.request;

import jakarta.validation.constraints.NotBlank;
import lems.cowshed.domain.post.comment.Comment;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentSaveRequest {

    @NotBlank
    private String context;

    @Builder
    private CommentSaveRequest(String context) {
        this.context = context;
    }

    public Comment toEntity(Long userId){
        return Comment.builder()
                .userId(userId)
                .context(this.context)
                .build();
    }
}
