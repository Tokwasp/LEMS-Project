package lems.cowshed.api.advice.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice(basePackages = "lems.cowshed.api.controller.event")
public class EventAdvice {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public EventErrorResult eventNotValidHandler(MethodArgumentNotValidException ex){
        BindingResult bindingResult = ex.getBindingResult();

        List<EventErrorMessage> errorMessages = bindingResult.getFieldErrors().stream()
                .map(FieldError -> new EventErrorMessage(FieldError.getField(), FieldError.getDefaultMessage()))
                .collect(Collectors.toList());

        return new EventErrorResult(errorMessages);
    }

    @Getter
    @AllArgsConstructor
    @Schema(description = "에러 객체")
    public static class EventErrorMessage{
        @Schema(description = "에러 필드", example = "name")
        String field;
        @Schema(description = "에러 내용", example = "null 값은 사용 할수 없습니다.")
        String message;
    }

    @Getter
    @AllArgsConstructor
    public static class EventErrorResult{
        List<EventErrorMessage> error;
    }
}
