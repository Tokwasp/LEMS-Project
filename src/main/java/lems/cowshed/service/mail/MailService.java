package lems.cowshed.service.mail;

import lems.cowshed.dto.user.verification.response.VerificationResultInfo;
import lems.cowshed.domain.mail.Mail;
import lems.cowshed.domain.mail.MailVerificationStorage;
import lems.cowshed.domain.mail.code.CodeType;
import lems.cowshed.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import static lems.cowshed.global.exception.Message.*;
import static lems.cowshed.global.exception.Reason.*;

@RequiredArgsConstructor
@Service
public class MailService {

    private final JavaMailSender javaMailSender;
    private final MailVerificationStorage mailVerificationStorage;

    public void sendCodeToMail(CodeType codeType, Mail mail) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        try {
            simpleMailMessage.setTo(mail.getMail());
            // 메일의 제목 설정
            simpleMailMessage.setSubject(codeType.getDescription());
            // 메일의 내용 설정
            simpleMailMessage.setText(mail.getCode());

            if(codeType.isSignUpCode()) {
                mailVerificationStorage.put(mail, nowDateTimePlusTenMinutes());
            }
            javaMailSender.send(simpleMailMessage);
        } catch (Exception e) {
            throw new BusinessException(MAIL, MAIL_SEND_ERROR);
        }

    }

    public VerificationResultInfo verifyMail(Mail mail, LocalDateTime currentDateTime) {
        LocalDateTime expirationDateTime = Optional.ofNullable(mailVerificationStorage.get(mail))
                .orElseThrow(() -> new BusinessException(MAIL_CERTIFICATION_CODE, MAIL_NOT_VALID_CERTIFICATION_CODE));

        return VerificationResultInfo.of(currentDateTime.isBefore(expirationDateTime));
    }

    public boolean isMailVerifyFail(Mail mail) {
        return !mailVerificationStorage.containsMail(mail);
    }

    private LocalDateTime nowDateTimePlusTenMinutes() {
        return LocalDateTime.now().plusMinutes(10);
    }
}