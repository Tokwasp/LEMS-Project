package lems.cowshed.api.advice.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice(basePackages = "lems.cowshed.api.controller.event")
public class EventAdvice implements EventAdSpecification{

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
    public static class EventErrorMessage{
        String fieldName;
        String message;
    }

    @Getter
    @AllArgsConstructor
    public static class EventErrorResult{
        @Schema(description = "검증에 실패 했습니다. 값을 확인 해주세요!",
                example = "[{\"fieldName\": \"name\", \"message\": \"공백일 수 없습니다\"}," +
                " {\"fieldName\": \"maxParticipants\"," +
                " \"message\": \"200 이하여야 합니다\"}]")
        List<EventErrorMessage> result;
    }
}
