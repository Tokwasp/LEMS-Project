package lems.cowshed.api.advice;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletResponse;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

//TODO
@RestControllerAdvice
public class GeneralAdvice {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<List<ErrorContent>> userNotValidHandler(MethodArgumentNotValidException ex){
        BindingResult bindingResult = ex.getBindingResult();

        List<ErrorContent> ErrorContents = bindingResult.getFieldErrors().stream()
                .map(FieldError -> new ErrorContent(FieldError.getField(), FieldError.getDefaultMessage()))
                .toList();

        return CommonResponse.of(HttpStatus.BAD_REQUEST, ErrorContents);
    }

    @ExceptionHandler(value = {BusinessException.class})
    public CommonResponse<ErrorContent> userNotValidHandler(BusinessException ex, HttpServletResponse response){
        HttpStatus httpStatus = ex.getHttpStatus();
        response.setStatus(httpStatus.value());
        return CommonResponse.of(httpStatus, new ErrorContent(ex.getReason(), ex.getMessage()));
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse<String> handleExceptionFromAPIMethod(Exception ex){
        return CommonResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "예상치 못한 예외 입니다.");
    }

    @Getter
    @AllArgsConstructor
    @Schema(description = "에러 내용")
    public static class ErrorContent{
        @Schema(description = "에러 필드", example = "name")
        String field;
        @Schema(description = "에러 메시지", example = "null 값은 사용 할수 없습니다.")
        String message;
    }

}
