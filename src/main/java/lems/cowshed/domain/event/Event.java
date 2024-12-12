package lems.cowshed.domain.event;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Event {

    @Id
    @Column(name = "event_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 20)
    private String name;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

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

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    public static Event of(String name, LocalDateTime eventDate, String location, String address, String author, String email, String content, int capacity, int applicants, Availability availability){
        return new Event(null, name, eventDate, location, address, author, email, content, capacity, applicants, availability, LocalDateTime.now(), LocalDateTime.now());
    }

}
