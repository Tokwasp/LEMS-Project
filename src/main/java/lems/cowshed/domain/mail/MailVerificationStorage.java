package lems.cowshed.domain.mail;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class MailVerificationStorage {
    private final Map<Mail, LocalDateTime> mailExpirationsPeriod;

    public MailVerificationStorage() {
        this.mailExpirationsPeriod = new HashMap<>();
    }

    public boolean containsMail(Mail mail){
        return mailExpirationsPeriod.containsKey(mail);
    }

    public void put(Mail mail, LocalDateTime dateTime){
        mailExpirationsPeriod.put(mail, dateTime);
    }

    public LocalDateTime get(Mail mail){
        return mailExpirationsPeriod.get(mail);
    }

    public void clear() {
        mailExpirationsPeriod.clear();
    }
}