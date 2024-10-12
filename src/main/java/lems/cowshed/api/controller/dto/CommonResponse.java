package lems.cowshed.api.controller.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommonResponse<T> {
    private final T data;
    private final int code;
    private final String message;

    public static <T> CommonResponse<T> success(T data){
        return new CommonResponse<>(data, Result.OK.getCode(), Result.OK.getMessage());
    }

    public static <T> CommonResponse<T> success(){
        return new CommonResponse<>(null,Result.OK.getCode(), Result.OK.getMessage());
    }
}
