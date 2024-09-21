package lems.cowshed.api.advice;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lems.cowshed.api.advice.GeneralAdvice;
import org.hibernate.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface GeneralAdSpecification {

    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "❌ 예상치 못한 예외 입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GeneralAdvice.Result.class)))
    })
    public GeneralAdvice.Result handleExceptionFromAPIMethod(Exception ex);
}
