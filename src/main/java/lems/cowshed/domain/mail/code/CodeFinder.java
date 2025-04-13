package lems.cowshed.domain.mail.code;

import org.springframework.stereotype.Component;

import java.util.List;

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
                .orElseThrow(() -> new IllegalArgumentException("찾을수 없는 코드 입니다."));
    }
}
