package lems.cowshed.domain.event;

import lems.cowshed.domain.UploadFile;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EventEditCommand {

    private String name;
    private Category category;
    private String content;
    private int capacity;
    private UploadFile uploadFile;

    @Builder
    private EventEditCommand(String name, Category category, String content, int capacity, UploadFile uploadFile) {
        this.name = name;
        this.category = category;
        this.content = content;
        this.capacity = capacity;
        this.uploadFile = uploadFile;
    }

    public static EventEditCommand of(String name, Category category, String content, int capacity, UploadFile uploadFile){
        return EventEditCommand.builder()
                .name(name)
                .category(category)
                .content(content)
                .capacity(capacity)
                .uploadFile(uploadFile)
                .build();
    }
}
