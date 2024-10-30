package lems.cowshed.domain.user;

import jakarta.persistence.*;
import lems.cowshed.api.controller.dto.user.request.UserEditRequestDto;
import lems.cowshed.domain.bookmark.Bookmark;
import lems.cowshed.domain.userevent.UserEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 45)
    private String username;

    private String password;

    private String role;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String email;

    private LocalDate birth;

    @Column(length = 100)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(length = 45)
    private Mbti mbti;

    @Column(length = 200)
    private String introduction;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

    public void setEditUser(UserEditRequestDto userEditRequestDto){
        this.username = userEditRequestDto.getUsername();
        this.introduction = userEditRequestDto.getIntroduction();
        this.location = userEditRequestDto.getLocalName();
        this.birth = userEditRequestDto.getBirth();
        this.mbti = userEditRequestDto.getCharacter();
    }

}