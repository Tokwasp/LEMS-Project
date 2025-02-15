package lems.cowshed.domain.event;

import jakarta.persistence.*;
import lems.cowshed.api.controller.dto.event.request.EventUpdateRequestDto;
import lems.cowshed.domain.BaseEntity;
import lombok.*;

import java.time.LocalDate;

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

    @Column(name = "event_date")
    private LocalDate eventDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Category category;

    @Column(name = "location", length = 20)
    private String location;

    @Column(name = "address", length = 45)
    private String address;

    @Column(name = "author", length = 20)
    private String author;

    @Column(name = "email",  length = 40)
    private String email;

    @Column(name = "content", length = 200)
    private String content;

    @Column(name = "capacity")
    private int capacity;

    @Column(name = "applicants")
    private int applicants;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability")
    private Availability availability;

    @Builder
    public Event(String name, LocalDate eventDate, Category category,
                 String location, String address, String author,
                 String email, String content, int capacity,
                 int applicants, Availability availability) {
        this.name = name;
        this.eventDate = eventDate;
        this.category = category;
        this.location = location;
        this.address = address;
        this.author = author;
        this.email = email;
        this.content = content;
        this.capacity = capacity;
        this.applicants = applicants;
        this.availability = availability;
    }

    public void update(String name, Category category, String location, LocalDate eventDate, int capacity, String content) {
        this.name = name;
        this.category = category;
        this.location = location;
        this.eventDate = eventDate;
        this.capacity = capacity;
        this.content = content;
    }

    public void edit(EventUpdateRequestDto requestDto) {
        if(requestDto.getName() != null) this.name = requestDto.getName();
        this.capacity = requestDto.getCapacity();
        if(requestDto.getContent() != null) this.content = requestDto.getContent();
        if(requestDto.getCategory() != null) this.category = requestDto.getCategory();
        if(requestDto.getEventDate() != null) this.eventDate = requestDto.getEventDate();
        if(requestDto.getLocation() != null) this.location = requestDto.getLocation();
    }

    public boolean isNotParticipate(long participateCount){
        return capacity == participateCount;
    }
}

