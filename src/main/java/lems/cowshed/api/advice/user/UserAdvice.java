package lems.cowshed.api.advice.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Order(0)
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

    @ExceptionHandler(value = {BusinessException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public UserErrorResult userBusinessHandler(BusinessException ex){
        log.info("예외 메시지 : {}", ex.getMessage(), ex);
        return new UserErrorResult(List.of(new UserErrorMessage(ex.getReason(), ex.getMessage())));
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
