package lems.cowshed.api.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lems.cowshed.api.dto.event.EventDto;
import lems.cowshed.api.dto.event.EventListDto;
import lems.cowshed.api.dto.event.EventSaveDto;
import lems.cowshed.api.dto.event.EventUpdateDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface EventSpecification {

    //모든 모임 목록 조회
    List<EventListDto> findAll();

    //카테고리별 조회
    List<EventListDto> findByCategory(@PathVariable String category);

    //검색어로 조회
    List<EventListDto> findByKeyword(@PathVariable String keyword);

    //모임 상세 조회
    @Operation(summary = "모임 상세 조회", description = "id 값에 해당 하는 모임을 찾아 상세 페이지를 출력한다.. ",
            responses = {
                    @ApiResponse(responseCode = "200", description = "⭕ SUCCESS",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = EventDto.class))),
            })
    EventDto findById(@PathVariable Long id);



    //모임 등록
    @Operation(summary = "모임 등록", description = "모임을 등록 합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "⭕ SUCCESS 모임 등록에 성공 했습니다!")
            })
    void save(@RequestBody EventSaveDto event);

    //모임 수정
    @Operation(summary = "모임 수정", description = "모임의 세부 사항을 수정 합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "⭕ SUCCESS 모임 수정에 성공 했습니다!")
            })
    void edit(@PathVariable Long id, @RequestBody @Validated EventUpdateDto eventUpdateDto);

    //모임 삭제
    @Operation(summary = "모임 삭제", description = "모임을 삭제 합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "⭕ SUCCESS 모임 삭제 성공 했습니다!")
            })
    void delete(@PathVariable Long id);
}
