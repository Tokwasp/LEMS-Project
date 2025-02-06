package lems.cowshed.config.swagger;

import lems.cowshed.api.controller.ErrorCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ErrorResponseDto {

    private ErrorCode httpStatus;
    private String code;
    private String message;
    private List<String> errors;

    public ErrorResponseDto(ErrorCode errorcode) {
        this.httpStatus = errorcode;
        this.code = errorcode.getCode();
        this.message = errorcode.getMessage();
        this.errors = new ArrayList<>();
    }

    public static ErrorResponseDto from(ErrorCode errorCode) {
        return new ErrorResponseDto(errorCode);
    }
}
