package lems.cowshed.api.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.dto.user.request.UserEditRequestDto;
import lems.cowshed.api.controller.dto.user.request.UserLoginRequestDto;
import lems.cowshed.api.controller.dto.user.request.UserSaveRequestDto;
import lems.cowshed.api.controller.dto.user.response.UserEventResponseDto;
import lems.cowshed.api.controller.dto.user.response.UserMyPageResponseDto;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name="user-controller", description="회원 API")
public interface UserSpecification {

    @Operation(summary = "회원 가입", description = "이메일/비밀번호 + 닉네임을 통해 회원 가입을 합니다. [회원 가입]",
            responses = {
                    @ApiResponse(responseCode = "200", description = "⭕ 회원 가입에 성공 했습니다."),
                    @ApiResponse(responseCode = "400", description = "❌ 유효 하지 않은 회원 가입 요청 입니다. ",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class)))
    })
    CommonResponse<Void> saveUser(@RequestBody UserSaveRequestDto userSaveRequestDto);

    @Operation(summary = "로그인", description = "이메일/비밀번호을 통해 로그인을 합니다. [로그인]",
            responses = {
                    @ApiResponse(responseCode = "200", description = "⭕ 로그인에 성공 했습니다."),
                    @ApiResponse(responseCode = "400", description = "❌ 아이디 혹은 비밀번호가 틀렸습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class)))
            })
    CommonResponse<Void> login (@RequestBody UserLoginRequestDto UserLoginRequestDto);

    @Operation(summary = "마이페이지 회원 조회", description = "자신의 마이 페이지 정보를 가져 옵니다. [마이 페이지]",
            responses = {
                    @ApiResponse(responseCode = "200", description = "⭕ 마이 페이지 조회에 성공 했습니다.")})
    CommonResponse<UserMyPageResponseDto> userMyPage();

    @Operation(summary = "회원 수정", description = "회원 정보를 수정한다. [마이 페이지 -> 프로필 편집]",
            responses = {
                    @ApiResponse(responseCode = "200", description = "⭕ 회원 수정에 성공 했습니다."),
                    @ApiResponse(responseCode = "400", description = "❌ 유효 하지 않은 회원 수정 요청 입니다. ",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class)))

    })
    CommonResponse<Void> editUser(@RequestBody UserEditRequestDto UserEditRequestDto);

    @Operation(summary = "모임 회원 조회", description = "특정 모임에 속한 다수 회원을 조회 합니다. [이벤트 상세 > 참여자 목록]",
        responses = {
            @ApiResponse(responseCode = "200", description = "⭕ 모임 회원 조회에 성공 했습니다.")})
    CommonResponse<UserEventResponseDto> findUserEvent();
}
