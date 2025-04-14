package lems.cowshed.domain.mail;

import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

@Getter
public class Mail {

    private String mail;
    private String code;

    @Builder
    private Mail(String mail, String code) {
        this.mail = mail;
        this.code = code;
    }

    public static Mail of(String email, String code){
        return Mail.builder()
                .mail(email)
                .code(code)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Mail mail = (Mail) o;
        return Objects.equals(this.mail, mail.mail) && Objects.equals(this.code, mail.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.mail, this.code);
    }
}
