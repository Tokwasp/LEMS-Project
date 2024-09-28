package lems.cowshed.api.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.user.Gender;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public interface UserSpecification {
    @Operation(summary = "모임 회원 조회", description = "특정 모임에 속한 회원을 조회한다. [이벤트 상세 > 참여자 목록]")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "200 ok 요청이 성공적으로 처리되었습니다.",
                    content = {@Content(mediaType = "application/json", array=@ArraySchema(schema=@Schema(implementation = EventUserDto.class)))})})

    List<EventUserDto> getUserByEvent(Long eventId);

    @Operation(summary = "마이페이지 회원 조회", description = "본인 정보를 가져온다. [마이페이지]")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "200 ok 요청이 성공적으로 처리되었습니다.",
                    content = {@Content(mediaType = "application/json", schema=@Schema(implementation = MyInfoDto.class))})})
    void getMyInfoById(Long userId);

    @Operation(summary = "회원 등록", description = "새로운 회원 정보를 저장한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "200 ok 요청이 성공적으로 처리되었습니다.",
                    content = {@Content(mediaType = "application/json", schema=@Schema(implementation = UserSaveRequestDto.class))})})
    void saveUser(UserSaveRequestDto userSaveRequestDto);

    @Operation(summary = "회원 수정", description = "회원 정보를 수정한다. [프로필 편집]")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "200 ok 요청이 성공적으로 처리되었습니다.",
                    content = {@Content(mediaType = "application/json", schema=@Schema(implementation = UserUpdateRequestDto.class))})})
    void editUser(Long userId, UserUpdateRequestDto userUpdateRequestDto);

    @Getter
    @Setter
    @Schema(description="특정 모임에 속한 회원")
    public static class EventUserDto{
        @Schema(description = "이름", example = "김철수", required = true)
        private String name;
        @Schema(description = "성별", example = "M", required = true)
        private Gender gender;
        @Schema(description = "소개", example = "성남시 분당구에 사는 직장인입니다.", required = true)
        private String introduction;
    }
    @Getter
    @Setter
    @Schema(description = "마이페이지 회원 정보")
    public static class MyInfoDto{
        @Schema(description = "이름", example = "김철수", required=true)
        private String name;
        @Schema(description = "성별", example = "M", required=true)
        private Gender gender;
        @Schema(description = "생년월일", example = "1999-05-22", required=true)
        private String birth;
        @Schema(description = "성격유형", example = "ISTP")
        private String character;
        @Schema(description = "참여 모임", example = "")
        private List<Event> joinEvents;
        @Schema(description = "북마크 모임", example = "김철수")
        private List<Event> bookmarkEvents;
    }
    @Getter
    @Setter
    @Schema(description = "회원 등록")
    public static class UserSaveRequestDto {
        @Schema(description = "이름", example = "김철수", required=true)
        private String name;
        @Schema(description = "성별", example = "M", required=true)
        private Gender gender;
        @Schema(description = "이메일", example = "cheolsukim@lems.com")
        private String email;
        @Schema(description = "생년월일", example = "1999-05-22", required=true)
        private String birth;
        @Schema(description = "지역명", example = "서울시", required=true)
        private String local_name;
        @Schema(description = "성격유형", example = "ISTP")
        private String character;
        @Schema(description = "소개", example = "성남시 분당구에 사는 직장인입니다.")
        private String introduction;
    }
    @Getter
    @Setter
    @Schema(description = "회원 수정")
    public static class UserUpdateRequestDto {
        @Schema(description = "소개", example = "성남시 분당구에 사는 직장인입니다.")
        private String introduction;
        @Schema(description = "지역명", example = "서울시", required=true)
        private String local_name;
        @Schema(description = "생년월일", example = "1999-05-22", required=true)
        private String birth;
        @Schema(description = "성격유형", example = "ISTP")
        private String character;
    }
}
