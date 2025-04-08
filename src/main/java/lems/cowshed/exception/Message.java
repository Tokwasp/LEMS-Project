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
    USER_NOT_VERIFY_PASSWORD("검증 비밀번호가 일치 하지 않습니다."),
    USER_NOT_CERTIFICATION_CODE("인증 번호가 일치 하지 않거나 유효시간이 지났습니다."),

    MAIL_SEND_ERROR("메일 보내기 실패 하였습니다."),
    MAIL_NOT_VALID_CERTIFICATION_CODE("유효 하지 않은 검증 코드 입니다."),

    BOOKMARK_NOT_FOUND("북마크를 찾지 못했습니다."),

    EVENT_NOT_FOUND("모임을 찾지 못했습니다."),
    EVENT_NOT_REGISTERED_BY_USER("회원이 등록한 모임이 아닙니다."),
    EVENT_CAPACITY_OVER("참여 가능한 회원 수를 초과 하였습니다."),

    USER_EVENT_NOT_FOUND("모임 참여 기록을 찾지 못했습니다.");
    private final String message;
}
