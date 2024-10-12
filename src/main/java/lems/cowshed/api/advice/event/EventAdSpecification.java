package lems.cowshed.api.advice.event;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lems.cowshed.api.advice.GeneralAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

public interface EventAdSpecification  {
    @ApiResponse(responseCode = "400", description = "❌ 모임 객체가 검증에 실패 했습니다.",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EventAdvice.EventErrorResult.class)))
    public EventAdvice.EventErrorResult eventNotValidHandler(MethodArgumentNotValidException ex);
}
