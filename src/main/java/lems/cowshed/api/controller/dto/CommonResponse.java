package lems.cowshed.api.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommonResponse<T> {

    private final T data;

    @Schema(example = "수정, 조회, 삭제, 등록 성공!")
    private final String message;

    public static <T> CommonResponse<T> success(T data){
        return new CommonResponse<>(data, Result.OK.getMessage());
    }

    public static <T> CommonResponse<T> success(){
        return new CommonResponse<>(null, Result.OK.getMessage());
    }

    public static <T> CommonResponse<T> customMessage(String message){
        return new CommonResponse<>(null, message);
    }

    public static <T> CommonResponse<T> customMessage(T data, String message){
        return new CommonResponse<>(data, message);
    }
}