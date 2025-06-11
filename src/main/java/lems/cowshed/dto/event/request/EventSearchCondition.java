package lems.cowshed.dto.event.request;

import lems.cowshed.domain.event.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EventSearchCondition {
    private String content;
    private Category category;

    @Builder
    private EventSearchCondition(String content, Category category) {
        this.content = content;
        this.category = category;
    }
}
