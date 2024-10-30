package lems.cowshed.api.advice.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice(basePackages = "lems.cowshed.api.controller.user")
public class UserAdvice {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public UserErrorResult userNotValidHandler(MethodArgumentNotValidException ex){
        BindingResult bindingResult = ex.getBindingResult();

        List<UserErrorMessage> errorMessages = bindingResult.getFieldErrors().stream()
                .map(FieldError -> new UserErrorMessage(FieldError.getField(), FieldError.getDefaultMessage()))
                .collect(Collectors.toList());

        return new UserErrorResult(errorMessages);
    }

    @Getter
    @AllArgsConstructor
    @Schema(description = "에러 객체")
    public static class UserErrorMessage{
        @Schema(description = "에러 필드", example = "name")
        String field;
        @Schema(description = "에러 내용", example = "null 값은 사용 할수 없습니다.")
        String message;
    }

    @Getter
    @AllArgsConstructor
    public static class UserErrorResult{
        List<UserErrorMessage> error;
    }
}
