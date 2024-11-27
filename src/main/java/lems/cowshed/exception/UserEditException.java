package lems.cowshed.exception;


import org.springframework.http.HttpStatus;

public class UserEditException extends BusinessException {

    public UserEditException(HttpStatus httpStatus, String reason, String message) {
        super(httpStatus, reason, message);
    }

}
