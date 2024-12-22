package lems.cowshed.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BusinessException {

    public NotFoundException(Reason reason, Message message) {
        super(HttpStatus.NOT_FOUND, reason, message);
    }
}
