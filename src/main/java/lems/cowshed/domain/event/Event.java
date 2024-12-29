package lems.cowshed.domain.event;

import jakarta.persistence.*;
import lems.cowshed.domain.BaseEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Event extends BaseEntity {

    @Id
    @Column(name = "event_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 20)
    private String name;

    @Column(name = "event_date")
    private LocalDateTime eventDate;
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
    
    public static Event of(String name, LocalDateTime eventDate, Category category, String location, String address, String author, String email, String content, int capacity, int applicants, Availability availability){
        return new Event(null, name, eventDate, category, location, address, author, email, content, capacity, applicants, availability);
    }

    public void update(String name, Category category, String location, LocalDateTime eventDate, int capacity, String content) {
        this.name = name;
        this.category = category;
        this.location = location;
        this.eventDate = eventDate;
        this.capacity = capacity;
        this.content = content;
    }
}
