package lems.cowshed.global.exception;

import lombok.Getter;

@Getter
public class BusinessException extends IllegalArgumentException{

    private final String reason;

    public BusinessException(Reason reason, Message message) {
        super(message.getMessage());
        this.reason = reason.getText();
    }

}
