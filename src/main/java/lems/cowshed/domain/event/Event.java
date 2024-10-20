package lems.cowshed.domain.event;

import jakarta.persistence.*;
import lems.cowshed.domain.userevent.UserEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
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

    /*@Enumerated(EnumType.STRING)
    @Column(name = "sorting_method")
    private SortingMethod sortingMethod;*/
}
