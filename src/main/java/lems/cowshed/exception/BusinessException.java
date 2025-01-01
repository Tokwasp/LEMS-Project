package lems.cowshed.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends IllegalArgumentException{

    private final String reason;

    public BusinessException(Reason reason, Message message) {
        super(message.getMessage());
        this.reason = reason.getText();
    }

}
