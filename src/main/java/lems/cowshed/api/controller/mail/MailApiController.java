package lems.cowshed.api.controller.mail;

import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.dto.mail.request.MailVerificationRequest;
import lems.cowshed.api.controller.dto.mail.response.MailExpirationInfo;
import lems.cowshed.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
        mailService.sendSignUpMessageToEmail(email);
        return CommonResponse.success();
    }

}