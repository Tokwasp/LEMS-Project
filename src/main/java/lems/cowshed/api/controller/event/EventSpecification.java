package lems.cowshed.api.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lems.cowshed.api.advice.event.EventAdvice;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface EventSpecification {
    //모임 단건 조회
    @Operation(summary = "모임 단건 조회", description = "id 값에 해당 하는 모임을 찾습니다. ",
            responses = {
                    @ApiResponse(responseCode = "200", description = "⭕ SUCCESS",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = EventController.eventDto.class))),
            })
    EventController.eventDto event(@PathVariable Long id);

    //모임 등록
    @Operation(summary = "모임 등록", description = "모임을 등록 합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "⭕ SUCCESS 모임 등록에 성공 했습니다!")
            })
    void saveEvent(@RequestBody EventController.saveEventDto event);

    //모임 수정
    @Operation(summary = "모임 수정", description = "모임의 세부 사항을 수정 합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "⭕ SUCCESS 모임 수정에 성공 했습니다!")
            })
    void editEvent(@PathVariable Long id);

    //모임 삭제
    @Operation(summary = "모임 삭제", description = "모임을 삭제 합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "⭕ SUCCESS 모임 삭제 성공 했습니다!")
            })
    void deleteEvent(@PathVariable Long id);
}
