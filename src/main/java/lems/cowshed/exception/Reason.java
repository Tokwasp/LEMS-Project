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

    BOOKMARK_ID("boomark"),

    EVENT_ID("event_id"),
    EVENT_CAPACITY("event_capacity"),
    BusinessReason("Business"),

    USER_EVENT("USER_EVENT");
    private final String text;
}
