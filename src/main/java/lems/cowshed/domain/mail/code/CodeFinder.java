package lems.cowshed.domain.mail.code;

import lems.cowshed.global.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.List;
import static lems.cowshed.global.exception.Message.*;
import static lems.cowshed.global.exception.Reason.*;

@Component
public class CodeFinder {

    private final List<CodeProvidable> CODE_PROVIDERS = List.of(
            new SignUpCodeProvider(),
            new PasswordCodeProvider()
    );

    public String findCodeFrom(CodeType codeType){
        return CODE_PROVIDERS.stream()
                .filter(provider -> provider.isSupport(codeType))
                .findFirst()
                .map(provider -> provider.provide(codeType))
                .orElseThrow(() -> new BusinessException(CODE_PROVIDER, CODE_PROVIDER_MISMATCH));
    }
}
