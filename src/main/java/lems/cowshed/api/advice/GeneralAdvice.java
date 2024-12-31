package lems.cowshed.api.advice;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletResponse;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.ErrorCode;
import lems.cowshed.api.controller.ErrorResponse;
import lems.cowshed.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class GeneralAdvice {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse<List<ErrorContent>>> userNotValidHandler(MethodArgumentNotValidException ex){
        BindingResult bindingResult = ex.getBindingResult();

        List<ErrorContent> ErrorContents = bindingResult.getFieldErrors().stream()
                .map(FieldError -> new ErrorContent(FieldError.getField(), FieldError.getDefaultMessage()))
                .toList();

        ErrorResponse<List<ErrorContent>> errorResponse = ErrorResponse.of(ErrorCode.ARGUMENT_VALID_ERROR, ErrorContents);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(value = {BusinessException.class})
    public ResponseEntity<ErrorResponse<ErrorContent>> handleBusinessException(BusinessException ex){
        ErrorResponse<ErrorContent> errorResponse = ErrorResponse.of(ErrorCode.BUSINESS_ERROR, new ErrorContent(ex.getReason(), ex.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    //TODO 데이터 == null 이면
    @ExceptionHandler(value = {Exception.class})
    public ErrorResponse<String> handleExceptionFromAPIMethod(Exception ex){
        return ErrorResponse.of(ErrorCode.INTERNAL_ERROR, "");
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