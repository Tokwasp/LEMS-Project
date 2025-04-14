package lems.cowshed.api.controller.mail;

import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.dto.verification.request.MailVerificationRequest;
import lems.cowshed.api.controller.dto.verification.response.VerificationResultInfo;
import lems.cowshed.domain.mail.Mail;
import lems.cowshed.domain.mail.code.CodeFinder;
import lems.cowshed.service.MailService;
import lems.cowshed.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static lems.cowshed.domain.mail.code.CodeType.*;

@RequiredArgsConstructor
@RequestMapping("/verification")
@RestController
public class VerificationApiController implements VerificationSpecification {

    private final UserService userService;
    private final MailService mailService;
    private final CodeFinder codeFinder;

    @PostMapping("/mail/{mail}")
    public CommonResponse<VerificationResultInfo> verifyMailAndSendCode(@PathVariable String mail){
        if(userService.isExistEmail(mail)){
            return CommonResponse.success(VerificationResultInfo.of(false));
        }

        String code = codeFinder.findCodeFrom(SIGN_UP);
        mailService.sendCodeToMail(SIGN_UP, Mail.of(mail, code));
        return CommonResponse.success(VerificationResultInfo.of(true));
    }

    @GetMapping("/mail")
    public CommonResponse<VerificationResultInfo> verifyMail(@RequestBody MailVerificationRequest request){
        return CommonResponse.success(mailService.verifyMail(request.toMail(), LocalDateTime.now()));
    }

    @GetMapping("/username/{username}")
    public CommonResponse<VerificationResultInfo> verifyUsername(@PathVariable String username){
        return CommonResponse.success(VerificationResultInfo.of(userService.isDuplicatedUsername(username)));
    }

}