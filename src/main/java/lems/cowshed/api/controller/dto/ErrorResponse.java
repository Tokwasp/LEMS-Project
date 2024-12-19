package lems.cowshed.api.controller.dto;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse<T> {

    private final T error;
    private final HttpStatus httpStatus;
    private final int code;

    public ErrorResponse(HttpStatus httpStatus, T error, int code) {
        this.error = error;
        this.httpStatus = httpStatus;
        this.code = code;
    }

    public static <T> ErrorResponse<T> of(HttpStatus httpStatus, T data){
        return new ErrorResponse<>(httpStatus, data, httpStatus.value());
    }

}
