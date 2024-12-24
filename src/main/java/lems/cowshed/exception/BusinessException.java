package lems.cowshed.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends IllegalArgumentException{

    private final HttpStatus httpStatus;
    private final String reason;

    public BusinessException(HttpStatus httpStatus, Reason reason, Message message) {
        super(message.getMessage());
        this.httpStatus = httpStatus;
        this.reason = reason.getText();
    }

}
