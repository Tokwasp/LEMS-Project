package lems.cowshed.domain.event;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import lems.cowshed.api.controller.dto.event.request.EventUpdateRequestDto;
import lems.cowshed.domain.BaseEntity;
import lems.cowshed.domain.UploadFile;
import lems.cowshed.domain.userevent.UserEvent;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Event extends BaseEntity {

    @Id
    @Column(name = "event_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 20)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Category category;

    @Column(name = "author", length = 20)
    private String author;

    @Column(name = "content", length = 200)
    private String content;

    @Max(100)
    @Column(name = "capacity")
    private int capacity;

    @Embedded
    private UploadFile uploadFile;

    @OneToMany(mappedBy = "event")
    private List<UserEvent> participants = new ArrayList<>();

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

    public void edit(EventUpdateRequestDto requestDto) {
        if(requestDto.getName() != null) this.name = requestDto.getName();
        this.capacity = requestDto.getCapacity();
        if(requestDto.getContent() != null) this.content = requestDto.getContent();
        if(requestDto.getCategory() != null) this.category = requestDto.getCategory();
    }

    public boolean isOverCapacity(long capacity){
        return this.capacity <= capacity;
    }

    public boolean isNotSameAuthor(String author) {
        return !this.author.equals(author);
    }

}