package lems.cowshed.domain.mail.code;

import lems.cowshed.global.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import static lems.cowshed.global.exception.Message.*;
import static lems.cowshed.global.exception.Reason.*;

@Component
public class SignUpCodeProvider implements CodeProvidable {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int CODE_LENGTH = 10;

    @Override
    public boolean isSupport(CodeType codeType) {
        return codeType.isSignUpCode();
    }

    @Override
    public String provide(CodeType codeType) {
        if(codeType.isNotSignUpCode()){
            throw new BusinessException(CODE_TYPE, CODE_TYPE_MISMATCH);
        }

        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomNum = RANDOM.nextInt(CODE_LENGTH);
            sb.append(randomNum);
        }
        return sb.toString();
    }

}