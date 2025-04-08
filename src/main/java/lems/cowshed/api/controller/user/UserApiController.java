package lems.cowshed.api.controller.user;

import jakarta.validation.Valid;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.dto.user.request.UserEditRequestDto;
import lems.cowshed.api.controller.dto.user.request.UserLoginRequestDto;
import lems.cowshed.api.controller.dto.user.request.UserSaveRequestDto;
import lems.cowshed.api.controller.dto.user.response.*;
import lems.cowshed.domain.mail.Mail;
import lems.cowshed.exception.BusinessException;
import lems.cowshed.service.CustomUserDetails;
import lems.cowshed.service.MailService;
import lems.cowshed.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static lems.cowshed.exception.Message.USER_NOT_CERTIFICATION_CODE;
import static lems.cowshed.exception.Reason.USER_CERTIFICATION_CODE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserApiController implements UserSpecification{

    private final UserService userService;
    private final MailService mailService;

    @GetMapping("/my-page")
    public CommonResponse<UserMyPageInfo> findMyPage(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        UserMyPageInfo myPage = userService.findMyPage(customUserDetails.getUserId());
        return CommonResponse.success(myPage);
    }

    @GetMapping("/events/{event-id}")
    public CommonResponse<ParticipatingUserListInfo> findParticipants(@PathVariable("event-id") Long eventId){
        ParticipatingUserListInfo participatingUser = userService.findParticipants(eventId);
        return CommonResponse.success(participatingUser);
    }

    @PostMapping("/signUp")
    public CommonResponse<Void> signUp(@Valid @RequestBody UserSaveRequestDto request) {
        if(mailService.isMailExpired(Mail.of(request.getEmail(),request.getCode()))){
            throw new BusinessException(USER_CERTIFICATION_CODE, USER_NOT_CERTIFICATION_CODE);
        }

        userService.signUp(request);
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

    @GetMapping("/validate/username/{username}")
    public CommonResponse<DuplicateCheckResult> findDuplicatedUsername(@PathVariable String username){
        return CommonResponse.success(DuplicateCheckResult.of(userService.signUpValidationForUsername(username)));
    }

    @GetMapping("/validate/email/{email}")
    public CommonResponse<DuplicateCheckResult> findDuplicatedEmail(@PathVariable String email){
        return CommonResponse.success(DuplicateCheckResult.of(userService.signUpValidationForEmail(email)));
    }

    @DeleteMapping
    public CommonResponse<Void> deleteUser(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        userService.deleteUser(customUserDetails.getUserId());
        return CommonResponse.success();
    }
}