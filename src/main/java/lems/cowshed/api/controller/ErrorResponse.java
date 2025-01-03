package lems.cowshed.api.controller;

import lombok.Getter;

@Getter
public class ErrorResponse <T> extends Response {

    private final T errors;

    // default 메세지
    public ErrorResponse(ErrorCode errorCode, T errors) {
        super(errorCode.getHttpStatus(), errorCode.getMessage());
        this.errors = errors;
    }

    public static <T> ErrorResponse<T> of(ErrorCode errorCode, T data){
        return new ErrorResponse<>(errorCode, data);
    }

}