package lems.cowshed.domain.event;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

/*@Getter
@Entity
@NoArgsConstructor
public class Event {
    @Id
    @Column(name = "event_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @Temporal(TemporalType.DATE)
    @Column(name = "event_date", nullable = false)
    private Date eventDate;

    @Column(name = "location", length = 20, nullable = false)
    private String location;

    @Column(name = "address", length = 45, nullable = false)
    private String address;

    @Column(name = "author", length = 20, nullable = false)
    private String author;

    @Column(name = "email",  length = 40, nullable = false)
    private String email;

    @Column(name = "content", length = 200, nullable = false)
    private String content;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Column(name = "applicants")
    private int applicants;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability", nullable = false)
    private Availability availability;

    @Column(name = "created_date", nullable = false)
    private Date createdDate;

    @Column(name = "last_modified_date", nullable = false)
    private Date lastModifiedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "sorting_method")
    private SortingMethod sortingMethod;
}*/
