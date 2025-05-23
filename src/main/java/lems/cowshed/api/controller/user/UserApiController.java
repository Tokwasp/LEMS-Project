package lems.cowshed.api.controller.user;

import jakarta.validation.Valid;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.dto.user.request.UserEditRequestDto;
import lems.cowshed.dto.user.request.UserLoginRequestDto;
import lems.cowshed.dto.user.request.UserSaveRequestDto;
import lems.cowshed.dto.user.response.UserInfo;
import lems.cowshed.dto.user.response.UserMyPageInfo;
import lems.cowshed.domain.mail.Mail;
import lems.cowshed.domain.mail.code.CodeFinder;
import lems.cowshed.domain.mail.code.CodeType;
import lems.cowshed.domain.user.User;
import lems.cowshed.exception.BusinessException;
import lems.cowshed.domain.user.CustomUserDetails;
import lems.cowshed.service.mail.MailService;
import lems.cowshed.service.user.UserService;
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
    private final CodeFinder codeFinder;

    @PostMapping("/signUp")
    public CommonResponse<Void> signUp(@Valid @RequestBody UserSaveRequestDto request) {
        Mail mail = Mail.of(request.getEmail(), request.getCode());

        if(mailService.isMailVerifyFail(mail)){
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

    @GetMapping("/my-page")
    public CommonResponse<UserMyPageInfo> findMyPage(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        UserMyPageInfo myPage = userService.findMyPage(customUserDetails.getUserId());
        return CommonResponse.success(myPage);
    }

    @PostMapping("/password-reset")
    public CommonResponse<Void> sendTemporaryPasswordToEmail(@RequestParam String email){
        User user = userService.findUserFrom(email);
        String password = codeFinder.findCodeFrom(CodeType.PASSWORD);

        mailService.sendCodeToMail(CodeType.PASSWORD, Mail.of(email, password));
        userService.modifyPassword(user, password);
        return CommonResponse.success();
    }

    @GetMapping
    public CommonResponse<UserInfo> findUser(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        UserInfo response = userService.findUser(customUserDetails.getUserId());
        return CommonResponse.success(response);
    }

    @DeleteMapping
    public CommonResponse<Void> deleteUser(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        userService.deleteUser(customUserDetails.getUserId());
        return CommonResponse.success();
    }
}