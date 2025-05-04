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
    USER_CERTIFICATION_CODE("user_certification_code"),

    MAIL("mail"),
    MAIL_CERTIFICATION_CODE("mail_certification_code"),

    CODE_TYPE("code_type"),
    CODE_PROVIDER("code_provider"),

    BOOKMARK_ID("boomark"),

    EVENT_ID("event_id"),
    EVENT_CAPACITY("event_capacity"),
    BusinessReason("Business"),

    EVENT_PARTICIPATION("event_participation"),

    REGULAR_EVENT_ID("regular_event_id"),
    REGULAR_EVENT_PARTICIPATION("regular_event_participation");
    private final String text;
}
