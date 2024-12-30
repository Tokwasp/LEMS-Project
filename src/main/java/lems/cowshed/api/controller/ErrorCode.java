package lems.cowshed.api.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    SUCCESS("200", HttpStatus.OK, "OK"),

    INTERNAL_ERROR("500", HttpStatus.INTERNAL_SERVER_ERROR, "서버에 오류가 발생 했습니다."),
    BUSINESS_ERROR("400", HttpStatus.BAD_REQUEST, "비즈니스 로직 오류가 발생 했습니다."),
    NOT_FOUND_ERROR("404", HttpStatus.NOT_FOUND, "자원을 찾는 과정중 문제가 발생 했습니다."),
    ARGUMENT_VALID_ERROR("400", HttpStatus.BAD_REQUEST, "입력 값 검증에 실패 했습니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    public String getMessage(Throwable throwable){
        return this.getMessage() + " - " + throwable.getMessage();
    }

}
