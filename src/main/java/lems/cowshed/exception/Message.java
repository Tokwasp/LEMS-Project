package lems.cowshed.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Message {
    USER_NOT_FOUND("회원을 찾지 못했습니다."),
    USER_EMAIL_NOT_FOUND("회원의 이메일을 찾지 못했습니다."),
    USER_ID_PASSWORD_CHECK("아이디와 비밀번호를 다시 확인 해주세요"),
    USERNAME_EXIST("이미 존재하는 닉네임 입니다."),
    USERNAME_OR_EMAIL_EXIST("이미 존재하는 닉네임 혹은 이메일 입니다."),
    USER_NOT_CERTIFICATION_CODE("인증 코드가 일치 하지 않습니다."),

    MAIL_SEND_ERROR("메일 보내기 실패 하였습니다."),
    MAIL_NOT_VALID_CERTIFICATION_CODE("유효 하지 않은 검증 코드 입니다."),

    CODE_TYPE_MISMATCH("코드 타입이 잘못 되었습니다."),
    CODE_PROVIDER_MISMATCH("코드 타입에 맞는 코드 생성기가 없습니다."),
    BOOKMARK_NOT_FOUND("북마크를 찾지 못했습니다."),

    EVENT_NOT_FOUND("모임을 찾지 못했습니다."),
    EVENT_NOT_REGISTERED_BY_USER("회원이 등록한 모임이 아닙니다."),
    EVENT_CAPACITY_OVER("참여 가능한 회원 수를 초과 하였습니다."),
    EVENT_INVALID_UPDATE_CAPACITY("모임에 참여한 회원이 최대 인원보다 더 많습니다."),

    EVENT_PARTICIPATION_FOUND("모임 참여 기록을 찾지 못했습니다."),
    EVENT_ALREADY_PARTICIPATION("모임에 이미 참여 중 입니다."),

    REGULAR_EVENT_NOT_FOUND("정기 모임을 찾지 못했습니다."),
    REGULAR_EVENT_NOT_POSSIBLE_PARTICIPATION("정기 모임 인원을 초과 하여 참석 실패 하였습니다."),
    REGULAR_EVENT_INVALID_UPDATE_CAPACITY("정기 모임에 참여한 회원이 최대 인원보다 더 많습니다."),

    POST_ID("게시글을 찾지 못했습니다."),

    COMMENT_ID("댓글을 찾지 못했습니다.");
    private final String message;
}
