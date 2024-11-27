package lems.cowshed.exception;

import org.springframework.http.HttpStatus;

public class UserLoginException extends BusinessException {

    public UserLoginException(HttpStatus httpStatus, String reason, String message) {
        super(httpStatus, reason, message);
    }
}
