package lems.cowshed.api.controller.user;

import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.dto.user.verification.request.MailVerificationRequest;
import lems.cowshed.dto.user.verification.response.VerificationResultInfo;
import lems.cowshed.domain.mail.Mail;
import lems.cowshed.domain.mail.code.CodeFinder;
import lems.cowshed.service.mail.MailService;
import lems.cowshed.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static lems.cowshed.domain.mail.code.CodeType.SIGN_UP;

@RequiredArgsConstructor
@RequestMapping("/users/verification")
@RestController
public class UserVerificationController implements UserVerificationSpecification {

    private final UserService userService;
    private final MailService mailService;
    private final CodeFinder codeFinder;

    @PostMapping("/email/{email}")
    public CommonResponse<VerificationResultInfo> verifyEmailAndSendCode(@PathVariable String email){
        if(userService.isExistEmail(email)){
            return CommonResponse.success(VerificationResultInfo.of(false));
        }

        String code = codeFinder.findCodeFrom(SIGN_UP);
        mailService.sendCodeToMail(SIGN_UP, Mail.of(email, code));
        return CommonResponse.success(VerificationResultInfo.of(true));
    }

    @PostMapping("/email/code")
    public CommonResponse<VerificationResultInfo> verifyEmail(@RequestBody MailVerificationRequest request){
        return CommonResponse.success(mailService.verifyMail(request.toMail(), LocalDateTime.now()));
    }

    @GetMapping("/username/{username}")
    public CommonResponse<VerificationResultInfo> verifyUsername(@PathVariable String username){
        boolean isNotDuplicated = !userService.isDuplicatedUsername(username);
        return CommonResponse.success(VerificationResultInfo.of(isNotDuplicated));
    }

}