package lems.cowshed.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Message {
    USER_NOT_FOUND("유저를 찾지 못했습니다."),
    USER_ID_PASSWORD_CHECK("아이디와 비밀번호를 다시 확인 해주세요"),
    USERNAME_EXIST("이미 존재하는 닉네임 입니다."),
    USERNAME_OR_EMAIL_EXIST("이미 존재하는 닉네임 혹은 이메일 입니다."),


    BOOKMARK_NOT_FOUND("북마크를 찾지 못했습니다."),

    EVENT_NOT_FOUND("모임을 찾지 못했습니다.");


    private final String message;
}
