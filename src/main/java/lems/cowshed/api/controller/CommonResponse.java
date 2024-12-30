package lems.cowshed.api.controller;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static lems.cowshed.api.controller.ResponseMessage.*;

@Getter
public class CommonResponse<T> extends Response{

    private final T data;

    public CommonResponse(HttpStatus httpStatus, T data, String message) {
        super(httpStatus, message);
        this.data = data;
    }

    public static <T> CommonResponse<T> of(HttpStatus status, T data, String message){
        return new CommonResponse<>(status, data, message);
    }

    public static <T> CommonResponse<T> of(HttpStatus status, T data){
        return of(status, data, status.name());
    }

    public static <T> CommonResponse<T> success(HttpStatus status, T data, String message){
        return new CommonResponse<>(status, data, message);
    }
    public static <T> CommonResponse<T> success(T data, String message){
        return success(HttpStatus.OK, data, message);
    }

    public static <T> CommonResponse<T> success(T data){
        return success(HttpStatus.OK, data, HttpStatus.OK.name());
    }

    public static <T> CommonResponse<T> success(){
        return success(HttpStatus.OK, null, SUCCESS.getMessage());
    }
}