package lems.cowshed.api.controller.dto.mail.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MailVerificationRequest {

    @Schema(description = "이메일", example = "test1234@naver.com")
    @NotBlank(message = "이메일 값은 필수 입니다.")
    private String email;

    @Schema(description = "인증코드", example = "1541213876")
    @NotBlank(message = "인증코드 값은 필수 입니다.")
    private String code;

    private MailVerificationRequest() {}

    public MailVerificationRequest(String email, String code) {
        this.email = email;
        this.code = code;
    }

}