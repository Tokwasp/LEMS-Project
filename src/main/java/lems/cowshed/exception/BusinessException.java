package lems.cowshed.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends IllegalArgumentException{

    private final HttpStatus httpStatus;
    private final String reason;

    public BusinessException(HttpStatus httpStatus, String reason, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
