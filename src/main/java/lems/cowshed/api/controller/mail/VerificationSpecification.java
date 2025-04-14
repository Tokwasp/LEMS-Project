package lems.cowshed.api.controller.mail;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.ErrorCode;
import lems.cowshed.api.controller.dto.verification.request.MailVerificationRequest;
import lems.cowshed.api.controller.dto.verification.response.VerificationResultInfo;
import lems.cowshed.config.swagger.ApiErrorCodeExample;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name="verification-controller", description="검증 API")
public interface VerificationSpecification {

    @Operation(summary = "인증 코드 전송", description = "메일 주소를 검증하고 인증 코드를 전송 합니다.")
    @ApiErrorCodeExample(ErrorCode.BUSINESS_ERROR)
    CommonResponse<VerificationResultInfo> verifyMailAndSendCode(@PathVariable String mail);

    @Operation(summary = "메일 검증", description = "메일 주소와 인증코드를 검증 합니다.")
    @ApiErrorCodeExample(ErrorCode.BUSINESS_ERROR)
    CommonResponse<VerificationResultInfo> verifyMail(@RequestBody MailVerificationRequest request);

    @Operation(summary = "닉네임 검증", description = "중복된 닉네임이 있는지 검증 합니다.")
    CommonResponse<VerificationResultInfo> verifyUsername(@PathVariable String username);
}
