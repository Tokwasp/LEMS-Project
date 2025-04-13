package lems.cowshed.domain.mail.code;

import lombok.Getter;

@Getter
public enum CodeType {
    SIGN_UP("회원가입 인증 코드"),
    PASSWORD("임시 비밀번호 발급");

    private final String description;

    CodeType(String description) {
        this.description = description;
    }

    public boolean isSignUpCode(){
        return this == SIGN_UP;
    }

    public boolean isPasswordCode() {
        return this == PASSWORD;
    }

    public boolean isNotSignUpCode(){
        return !isSignUpCode();
    }

    public boolean isNotPasswordCode(){
        return !isPasswordCode();
    }
}
