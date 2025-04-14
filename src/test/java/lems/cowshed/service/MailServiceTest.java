package lems.cowshed.service;

import lems.cowshed.api.controller.dto.verification.response.VerificationResultInfo;
import lems.cowshed.domain.mail.Mail;
import lems.cowshed.domain.mail.MailVerificationStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class MailServiceTest {

    @Autowired
    MailService mailService;

    @Autowired
    MailVerificationStorage mailVerificationStorage;

    @DisplayName("회원 가입 시 검증 만료 시간 전이라면 메일 검증에 통과한다.")
    @Test
    void verifyMail() {
        //given
        Mail mail = createMail("test@naver.com", "testCode");
        LocalDateTime expirationDateTime = LocalDateTime.of(2025, 4, 14, 12,0);
        LocalDateTime currentDateTime = LocalDateTime.of(2025, 4, 14, 11, 59);
        mailVerificationStorage.put(mail, expirationDateTime);

        //when
        VerificationResultInfo result = mailService.verifyMail(mail,currentDateTime);

        //then
        assertThat(result.isVerificationPassed()).isTrue();
    }

    @DisplayName("회원 가입 시 검증 만료 시간과 같다면 메일 검증에 실패 한다.")
    @Test
    void verifyMail_WhenCurrentTimeEqualsExpirationTime_ShouldFail() {
        //given
        Mail mail = createMail("test@naver.com", "testCode");
        LocalDateTime expirationDateTime = LocalDateTime.of(2025, 4, 14, 12,0);
        LocalDateTime currentDateTime = LocalDateTime.of(2025, 4, 14, 12, 0);
        mailVerificationStorage.put(mail, expirationDateTime);

        //when
        VerificationResultInfo result = mailService.verifyMail(mail,currentDateTime);

        //then
        assertThat(result.isVerificationPassed()).isFalse();
    }

    private Mail createMail(String email, String code){
        return Mail.of(email, code);
    }
}