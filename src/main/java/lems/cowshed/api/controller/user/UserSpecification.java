package lems.cowshed.api.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lems.cowshed.dto.user.response.UserInfo;
import lems.cowshed.dto.user.response.UserMyPageInfo;
import lems.cowshed.config.swagger.ApiErrorCodeExample;
import lems.cowshed.config.swagger.ApiErrorCodeExamples;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.ErrorCode;
import lems.cowshed.dto.user.request.UserModifyRequest;
import lems.cowshed.dto.user.request.UserLoginRequest;
import lems.cowshed.dto.user.request.UserSaveRequest;
import lems.cowshed.domain.user.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name="user", description="회원 API")
public interface UserSpecification {

    @Operation(summary = "회원 가입", description = "회원정보를 받아 회원 가입을 합니다. [회원 가입]")
    @ApiErrorCodeExample(ErrorCode.BUSINESS_ERROR)
    CommonResponse<Void> signUp(@RequestBody UserSaveRequest userSaveRequest);

    @Operation(summary = "로그인", description = "이메일/비밀번호을 통해 로그인을 합니다. [로그인]")
    @ApiErrorCodeExamples({ErrorCode.BUSINESS_ERROR, ErrorCode.NOT_FOUND_ERROR})
    CommonResponse<Void> login (@RequestBody UserLoginRequest UserLoginRequest);

    @Operation(summary = "회원 수정", description = "회원 정보를 수정한다. [마이 페이지 -> 프로필 편집]")
    @ApiErrorCodeExamples({ErrorCode.BUSINESS_ERROR, ErrorCode.NOT_FOUND_ERROR})
    CommonResponse<Void> editUser(@RequestBody UserModifyRequest UserModifyRequest,
                                  @AuthenticationPrincipal CustomUserDetails customUserDetails);

    @Operation(summary = "회원 이메일 임시 비밀번호 전송", description = "회원의 메일로 임시 비밀번호를 전송 합니다.")
    @ApiErrorCodeExamples({ErrorCode.BUSINESS_ERROR, ErrorCode.NOT_FOUND_ERROR})
    CommonResponse<Void> sendTemporaryPasswordToEmail(@RequestParam String email);

    @Operation(summary = "회원 조회", description = "회원의 세부 사항을 조회 합니다.")
    @ApiErrorCodeExample(ErrorCode.NOT_FOUND_ERROR)
    CommonResponse<UserInfo> findUser(@AuthenticationPrincipal CustomUserDetails customUserDetails);

    @Operation(summary = "내 정보 조회", description = "내 정보, 북마크 목록, 참가한 이벤트 조회")
    CommonResponse<UserMyPageInfo> findMyPage(@AuthenticationPrincipal CustomUserDetails customUserDetails);

    @Operation(summary = "회원 삭제", description = "회원을 삭제 합니다.")
    CommonResponse<Void> deleteUser(@AuthenticationPrincipal CustomUserDetails customUserDetails);
}