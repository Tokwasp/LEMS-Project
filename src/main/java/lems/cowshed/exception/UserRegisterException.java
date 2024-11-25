package lems.cowshed.exception;

import org.springframework.http.HttpStatus;

public class UserRegisterException extends BusinessException {

    public UserRegisterException(HttpStatus httpStatus, String reason, String message) {
        super(httpStatus, reason, message);
    }
}
