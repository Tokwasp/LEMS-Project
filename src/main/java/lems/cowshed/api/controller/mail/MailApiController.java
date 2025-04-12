package lems.cowshed.api.controller.mail;

import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.dto.mail.request.MailVerificationRequest;
import lems.cowshed.api.controller.dto.mail.response.MailExpirationInfo;
import lems.cowshed.domain.mail.Mail;
import lems.cowshed.domain.mail.code.CodeType;
import lems.cowshed.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static lems.cowshed.domain.mail.code.CodeType.*;

@RequiredArgsConstructor
@RequestMapping("/mails")
@RestController
public class MailApiController implements MailSpecification{

    private final MailService mailService;

    @GetMapping("/verification")
    public CommonResponse<MailExpirationInfo> verifyMail(@RequestBody MailVerificationRequest request){
        return CommonResponse.success(mailService.verifyMail(request));
    }

    @PostMapping("/verification/{email}")
    public CommonResponse<Void> sendVerifyCode(@PathVariable String email){
        String code = mailService.findCodeFrom(SIGN_UP);
        mailService.sendCodeToMail(SIGN_UP, Mail.of(email, code));
        return CommonResponse.success();
    }

}