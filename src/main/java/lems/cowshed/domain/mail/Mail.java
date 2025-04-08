package lems.cowshed.domain.mail;

import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

@Getter
public class Mail {

    private String email;
    private String code;

    @Builder
    private Mail(String email, String code) {
        this.email = email;
        this.code = code;
    }

    public static Mail of(String email, String code){
        return Mail.builder()
                .email(email)
                .code(code)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Mail another = (Mail) o;
        return Objects.equals(email, another.email) && Objects.equals(code, another.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, code);
    }
}
