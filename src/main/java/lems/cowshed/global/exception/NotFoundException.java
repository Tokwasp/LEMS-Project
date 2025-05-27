package lems.cowshed.global.exception;

public class NotFoundException extends BusinessException {

    public NotFoundException(Reason reason, Message message) {
        super(reason, message);
    }
}
