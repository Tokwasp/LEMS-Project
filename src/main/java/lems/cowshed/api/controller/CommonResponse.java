package lems.cowshed.api.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static lems.cowshed.api.controller.ResponseMessage.*;

@Getter
public class CommonResponse<T> {

    private int code;
    private HttpStatus status;
    private final T data;
    @Schema(example = "수정, 조회, 삭제, 등록 성공!")
    private final String message;

    public CommonResponse(HttpStatus status, T data, String message) {
        this.code = status.value();
        this.status = status;
        this.data = data;
        this.message = message;
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