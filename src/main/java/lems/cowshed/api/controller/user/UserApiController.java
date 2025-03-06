package lems.cowshed.api.controller.user;

import jakarta.validation.Valid;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.dto.user.request.UserEditRequestDto;
import lems.cowshed.api.controller.dto.user.request.UserLoginRequestDto;
import lems.cowshed.api.controller.dto.user.request.UserSaveRequestDto;
import lems.cowshed.api.controller.dto.user.response.ParticipatingUserListInfo;
import lems.cowshed.api.controller.dto.user.response.UserMyPageInfo;
import lems.cowshed.api.controller.dto.user.response.UserInfo;
import lems.cowshed.api.controller.dto.user.response.UserSignUpValidationInfo;
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
    public CommonResponse<UserMyPageInfo> findMyPage(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        UserMyPageInfo myPage = userService.findMyPage(customUserDetails.getUserId());
        return CommonResponse.success(myPage);
    }

    @GetMapping("/{event-id}")
    public CommonResponse<ParticipatingUserListInfo> findUserParticipatingInEvent(@PathVariable("event-id") long eventId,
                                                                                  @AuthenticationPrincipal CustomUserDetails customUserDetails){
        LocalDate now = LocalDate.now();
        ParticipatingUserListInfo userEvent = userService.findUserParticipatingInEvent(now, customUserDetails.getUserId());
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
    public CommonResponse<UserInfo> findUser(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        UserInfo response = userService.findUser(customUserDetails.getUserId());
        return CommonResponse.success(response);
    }

    @GetMapping("/username/{username}")
    public CommonResponse<UserSignUpValidationInfo> signUpValidationForUsername(@PathVariable String username){
        UserSignUpValidationInfo response = UserSignUpValidationInfo.from(userService.signUpValidationForUsername(username));
        return CommonResponse.success(response);
    }

    @GetMapping("/email/{email}")
    public CommonResponse<UserSignUpValidationInfo> signUpValidationForEmail(@PathVariable String email){
        UserSignUpValidationInfo response = UserSignUpValidationInfo.from(userService.signUpValidationForEmail(email));
        return CommonResponse.success(response);
    }

    @DeleteMapping
    public CommonResponse<Void> deleteUser(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        userService.deleteUser(customUserDetails.getUserId());
        return CommonResponse.success();
    }
}