package lems.cowshed.api.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.ErrorCode;
import lems.cowshed.api.controller.dto.user.request.UserEditRequestDto;
import lems.cowshed.api.controller.dto.user.request.UserLoginRequestDto;
import lems.cowshed.api.controller.dto.user.request.UserSaveRequestDto;
import lems.cowshed.api.controller.dto.user.response.UserEventResponseDto;
import lems.cowshed.api.controller.dto.user.response.UserMyPageResponseDto;
import lems.cowshed.api.controller.dto.user.response.UserResponseDto;
import lems.cowshed.api.controller.dto.user.response.UserSignUpValidationDto;
import lems.cowshed.config.swagger.ApiErrorCodeExample;
import lems.cowshed.service.CustomUserDetails;
import lems.cowshed.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserApiController implements UserSpecification{

    private final UserService userService;

    @GetMapping("/my-page")
    public CommonResponse<UserMyPageResponseDto> findMyPage(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        UserMyPageResponseDto myPage = userService.findMyPage(customUserDetails.getUserId());
        return CommonResponse.success(myPage);
    }

    @GetMapping("/{event-id}")
    public CommonResponse<UserEventResponseDto> findUserParticipatingInEvent(@PathVariable("event-id") long eventId,
                                                                             @AuthenticationPrincipal CustomUserDetails customUserDetails){
        LocalDate now = LocalDate.now();
        UserEventResponseDto userEvent = userService.findUserParticipatingInEvent(now, customUserDetails.getUserId());
        return CommonResponse.success(userEvent);
    }

    @PostMapping("/signUp")
    public CommonResponse<Void> signUp(@Valid @RequestBody UserSaveRequestDto userSaveRequestDto) {
        userService.signUp(userSaveRequestDto);
        return CommonResponse.success();
    }

    @PostMapping("/login")
    public CommonResponse<Void> login(@Valid @RequestBody UserLoginRequestDto userLoginRequestDto){
        userService.login(userLoginRequestDto);
        return CommonResponse.success();
    }

    @PatchMapping
    public CommonResponse<Void> editUser(@RequestBody UserEditRequestDto userEditRequestDto,
                                         @AuthenticationPrincipal CustomUserDetails customUserDetails){
        userService.editUser(userEditRequestDto, customUserDetails.getUserId(), customUserDetails.getUsername());
        return CommonResponse.success();
    }

    @GetMapping
    public CommonResponse<UserResponseDto> findUser(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        UserResponseDto response = userService.findUser(customUserDetails.getUserId());
        return CommonResponse.success(response);
    }

    @GetMapping("/username/{username}")
    public CommonResponse<UserSignUpValidationDto> signUpValidationForUsername(@PathVariable String username){
        UserSignUpValidationDto response = UserSignUpValidationDto.from(userService.signUpValidationForUsername(username));
        return CommonResponse.success(response);
    }

    @GetMapping("/email/{email}")
    public CommonResponse<UserSignUpValidationDto> signUpValidationForEmail(@PathVariable String email){
        UserSignUpValidationDto response = UserSignUpValidationDto.from(userService.signUpValidationForEmail(email));
        return CommonResponse.success(response);
    }

}