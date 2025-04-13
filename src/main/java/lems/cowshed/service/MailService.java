package lems.cowshed.service;

import lems.cowshed.api.controller.dto.mail.request.MailVerificationRequest;
import lems.cowshed.api.controller.dto.mail.response.MailExpirationInfo;
import lems.cowshed.domain.mail.Mail;
import lems.cowshed.domain.mail.code.CodeFinder;
import lems.cowshed.domain.mail.code.CodeType;
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
    private final CodeFinder codeFinder;
    private final Map<Mail, LocalDateTime> mailExpirationsPeriod = new HashMap<>();

    public void sendCodeToMail(CodeType codeType, Mail mail) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        try {
            simpleMailMessage.setTo(mail.getEmail());
            // 메일의 제목 설정
            simpleMailMessage.setSubject(codeType.getDescription());
            // 메일의 내용 설정
            simpleMailMessage.setText(mail.getCode());

            if(codeType.isSignUpCode()) {
                mailExpirationsPeriod.put(mail, nowDateTimePlusTenMinutes());
            }
            javaMailSender.send(simpleMailMessage);
        } catch (Exception e) {
            throw new BusinessException(MAIL, MAIL_SEND_ERROR);
        }

    }

    public String findCodeFrom(CodeType codeType){
        return codeFinder.findCodeFrom(codeType);
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