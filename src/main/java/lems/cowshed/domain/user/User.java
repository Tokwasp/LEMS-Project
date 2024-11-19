package lems.cowshed.domain.user;

import jakarta.persistence.*;

import lems.cowshed.api.controller.dto.user.request.UserEditRequestDto;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Builder
    private User(Long id, String username, String password, String role, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
    }

    public static User registerUser(String username, String password, String email, String role) {
        return User.builder()
                .username(username)
                .password(password)
                .email(email)
                .role(role)
                .build();
    }

    public static User createUserForDetails(Long id, String username, String password, String role, String email) {
        return User.builder()
                .id(id)
                .username(username)
                .password(password)
                .role(role)
                .email(email)
                .build();
    }

    public void modifyDetails(UserEditRequestDto userEditRequestDto){
        this.username = userEditRequestDto.getUsername();
        this.introduction = userEditRequestDto.getIntroduction();
        this.location = userEditRequestDto.getLocalName();
        this.birth = userEditRequestDto.getBirth();
        this.mbti = userEditRequestDto.getCharacter();
    }

}