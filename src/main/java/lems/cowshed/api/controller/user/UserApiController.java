package lems.cowshed.api.controller.user;

import jakarta.validation.Valid;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.domain.mail.Mail;
import lems.cowshed.domain.user.CustomUserDetails;
import lems.cowshed.dto.user.request.UserLoginRequest;
import lems.cowshed.dto.user.request.UserModifyRequest;
import lems.cowshed.dto.user.request.UserSaveRequest;
import lems.cowshed.dto.user.response.UserInfo;
import lems.cowshed.dto.user.response.UserMyPageInfo;
import lems.cowshed.global.exception.BusinessException;
import lems.cowshed.service.mail.MailService;
import lems.cowshed.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static lems.cowshed.global.exception.Message.USER_NOT_CERTIFICATION_CODE;
import static lems.cowshed.global.exception.Reason.USER_CERTIFICATION_CODE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserApiController implements UserSpecification {

    private final UserService userService;
    private final MailService mailService;

    @PostMapping("/signUp")
    public CommonResponse<Void> signUp(@Valid @RequestBody UserSaveRequest request) {
        Mail mail = Mail.of(request.getEmail(), request.getCode());

        if (mailService.isMailVerifyFail(mail)) {
            throw new BusinessException(USER_CERTIFICATION_CODE, USER_NOT_CERTIFICATION_CODE);
        }

        userService.signUp(request);
        return CommonResponse.success();
    }

    @PostMapping("/login")
    public CommonResponse<Void> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        userService.login(userLoginRequest);
        return CommonResponse.success();
    }

    @PutMapping
    public CommonResponse<Void> editUser(@RequestBody UserModifyRequest userModifyRequest,
                                         @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        userService.editUser(userModifyRequest, customUserDetails.getUserId());
        return CommonResponse.success();
    }

    @GetMapping("/my-page")
    public CommonResponse<UserMyPageInfo> findMyPage(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        UserMyPageInfo myPage = userService.findMyPage(customUserDetails.getUserId());
        return CommonResponse.success(myPage);
    }

    @PostMapping("/password-reset")
    public CommonResponse<Void> sendTemporaryPasswordToEmail(@RequestParam String email) {
        userService.sendTemporaryPasswordToEmail(email);
        return CommonResponse.success();
    }

    @GetMapping
    public CommonResponse<UserInfo> findUser(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        UserInfo response = userService.findUser(customUserDetails.getUserId());
        return CommonResponse.success(response);
    }

    @DeleteMapping
    public CommonResponse<Void> deleteUser(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        userService.deleteUser(customUserDetails.getUserId());
        return CommonResponse.success();
    }
}