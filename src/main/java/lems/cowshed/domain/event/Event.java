package lems.cowshed.domain.event;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import lems.cowshed.domain.BaseEntity;
import lems.cowshed.domain.UploadFile;
import lems.cowshed.domain.bookmark.Bookmark;
import lems.cowshed.global.exception.BusinessException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static lems.cowshed.global.exception.Message.EVENT_INVALID_UPDATE_CAPACITY;
import static lems.cowshed.global.exception.Reason.EVENT_PARTICIPATION;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Event extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String author;

    private String content;

    @Max(100)
    private int capacity;

    @Embedded
    private UploadFile uploadFile;

    @Version
    private long version;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @Builder
    public Event(String name, Category category, String author, String content,
                 int capacity, UploadFile uploadFile) {
        this.name = name;
        this.category = category;
        this.author = author;
        this.content = content;
        this.capacity = capacity;
        this.uploadFile = uploadFile;
    }

    public void modify(String name, int capacity, String content,
                       Category category, UploadFile uploadFile, int participantsCount) {
        this.name = name;
        this.category = category;
        this.content = content;
        updateCapacity(participantsCount, capacity);

        if (uploadFile != null) {
            this.updateUploadFile(uploadFile);
        }
    }

    public void updateCapacity(long participantsCount, int updateCapacity) {
        if (participantsCount > updateCapacity) {
            throw new BusinessException(EVENT_PARTICIPATION, EVENT_INVALID_UPDATE_CAPACITY);
        }
        this.capacity = updateCapacity;
    }

    public boolean isOverCapacity(long capacity) {
        return this.capacity <= capacity;
    }

    public boolean isNotSameAuthor(String author) {
        return !this.author.equals(author);
    }

    public void updateUploadFile(UploadFile uploadFile) {
        this.uploadFile = uploadFile;
    }
}