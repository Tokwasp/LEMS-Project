package lems.cowshed.domain.user;

import jakarta.persistence.*;

import lems.cowshed.api.controller.dto.user.request.UserEditRequestDto;
import lems.cowshed.domain.BaseEntity;
import lems.cowshed.domain.bookmark.Bookmark;
import lems.cowshed.domain.userevent.UserEvent;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 45)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @Builder
    private User(Long id, String username, String password,
                 Role role, String email, Mbti mbti,
                 String location, LocalDate birth, Gender gender) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.mbti = mbti;
        this.location = location;
        this.birth = birth;
        this.gender = gender;
        this.role = role;
        this.email = email;
    }

    public static User createUserForDetails(Long id, String username, String password, Role role, String email) {
        return User.builder()
                .id(id)
                .username(username)
                .password(password)
                .role(role)
                .email(email)
                .build();
    }

    //TODO
    public void modifyContents(UserEditRequestDto userEditRequestDto){
        if(userEditRequestDto.getUsername() != null) {this.username = userEditRequestDto.getUsername();}
        if(userEditRequestDto.getIntroduction() != null) {this.introduction = userEditRequestDto.getIntroduction();}
        if(userEditRequestDto.getLocalName() != null) {this.location = userEditRequestDto.getLocalName();}
        if(userEditRequestDto.getBirth() != null) {this.birth = userEditRequestDto.getBirth();}
        if(userEditRequestDto.getMbti() != null) {this.mbti = userEditRequestDto.getMbti();}
    }

}