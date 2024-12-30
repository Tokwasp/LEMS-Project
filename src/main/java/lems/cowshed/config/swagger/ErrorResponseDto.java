package lems.cowshed.config.swagger;

import lems.cowshed.api.controller.ErrorCode;
import lombok.Getter;

@Getter
public class ErrorResponseDto {

    private final ErrorCode errorcode;
    private String code;
    private String message;

    public ErrorResponseDto(ErrorCode errorcode) {
        this.errorcode = errorcode;
        this.code = errorcode.getCode();
        this.message = errorcode.getMessage();
    }

    public static ErrorResponseDto from(ErrorCode errorCode) {
        return new ErrorResponseDto(errorCode);
    }
}
