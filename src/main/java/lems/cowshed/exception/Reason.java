package lems.cowshed.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Reason {
    USER_ID("user_id"),
    USER_EMAIL("user_email"),
    USER_NAME("username"),
    USER_PASSWORD("user_password"),
    USERNAME_OR_EMAIL("usernameOrEmail"),
    USER_VERIFY_PASSWORD("user_verify_password"),
    USER_CERTIFICATION_CODE("user_certification_code"),

    MAIL("mail"),
    MAIL_CERTIFICATION_CODE("mail_certification_code"),

    CODE_TYPE("code_type"),

    BOOKMARK_ID("boomark"),

    EVENT_ID("event_id"),
    EVENT_CAPACITY("event_capacity"),
    BusinessReason("Business"),

    USER_EVENT("USER_EVENT");
    private final String text;
}
