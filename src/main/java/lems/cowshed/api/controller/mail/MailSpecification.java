package lems.cowshed.api.controller.mail;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.ErrorCode;
import lems.cowshed.api.controller.dto.mail.request.MailVerificationRequest;
import lems.cowshed.api.controller.dto.mail.response.MailExpirationInfo;
import lems.cowshed.config.swagger.ApiErrorCodeExample;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name="mail-controller", description="메일 API")
public interface MailSpecification {

    @Operation(summary = "인증 코드 전송", description = "이메일로 인증 코드를 보냅니다.")
    CommonResponse<Void> sendVerifyCode(@PathVariable String email);

    @Operation(summary = "메일 검증", description = "이메일로 보낸 인증코드를 검증 합니다.")
    @ApiErrorCodeExample(ErrorCode.BUSINESS_ERROR)
    CommonResponse<MailExpirationInfo> verifyMail(@RequestBody MailVerificationRequest request);
}
