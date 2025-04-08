package lems.cowshed.service;

import lems.cowshed.api.controller.dto.mail.request.MailVerificationRequest;
import lems.cowshed.api.controller.dto.mail.response.MailExpirationInfo;
import lems.cowshed.domain.mail.Mail;
import lems.cowshed.domain.mail.RandomIntCodeGenerator;
import lems.cowshed.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static lems.cowshed.exception.Message.*;
import static lems.cowshed.exception.Reason.*;

@RequiredArgsConstructor
@Service
public class MailService {

    private final JavaMailSender javaMailSender;
    private final RandomIntCodeGenerator stringGenerator;
    private final Map<Mail, LocalDateTime> mailExpirationsPeriod = new HashMap<>();

    public void sendSignUpMessageToEmail(String email) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        try {
            simpleMailMessage.setTo(email);

            // 메일의 제목 설정
            simpleMailMessage.setSubject("회원가입 인증 번호");

            // 메일의 내용 설정
            String code = stringGenerator.generateRandomString();
            simpleMailMessage.setText(code);

            mailExpirationsPeriod.put(Mail.of(email, code), nowDateTimePlusTenMinutes());
            javaMailSender.send(simpleMailMessage);

        } catch (Exception e) {
            throw new BusinessException(MAIL, MAIL_SEND_ERROR);
        }

    }

    public MailExpirationInfo verifyMail(MailVerificationRequest request) {
        Mail mail = Mail.of(request.getEmail(), request.getCode());
        return MailExpirationInfo.of(isMailExpired(mail));
    }

    public boolean isMailExpired(Mail mail) {
        LocalDateTime expirationsPeriod = Optional.ofNullable(mailExpirationsPeriod.get(mail))
                .orElseThrow(() -> new BusinessException(MAIL_CERTIFICATION_CODE, MAIL_NOT_VALID_CERTIFICATION_CODE));

        return isOverExpirationPeriod(expirationsPeriod);
    }

    private LocalDateTime nowDateTimePlusTenMinutes() {
        return LocalDateTime.now().plusMinutes(10);
    }

    private boolean isOverExpirationPeriod(LocalDateTime expirationDateTime) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return currentDateTime.isAfter(expirationDateTime);
    }

}