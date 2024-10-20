package lems.cowshed.api.advice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GeneralAdvice {

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleExceptionFromAPIMethod(Exception ex){
        return new Result(ex.getMessage());}

    @Data
    @AllArgsConstructor
    public static class Result {
        @Schema(description = "메시지", example = "예상치 못한 예외 입니다.")
        String message;
    }
}
