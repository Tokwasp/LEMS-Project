package lems.cowshed.domain.mail.code;

import lems.cowshed.global.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

import static lems.cowshed.global.exception.Message.CODE_TYPE_MISMATCH;
import static lems.cowshed.global.exception.Reason.CODE_TYPE;

@Component
public class PasswordCodeProvider implements CodeProvidable {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int PASSWORD_LENGTH = 15;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public boolean isSupport(CodeType codeType) {
        return codeType.isPasswordCode();
    }

    @Override
    public String provide(CodeType codeType) {
        if(codeType.isNotPasswordCode()){
            throw new BusinessException(CODE_TYPE, CODE_TYPE_MISMATCH);
        }

        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
}
