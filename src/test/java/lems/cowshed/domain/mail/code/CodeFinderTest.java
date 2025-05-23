package lems.cowshed.domain.mail.code;

import lems.cowshed.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class CodeFinderTest extends IntegrationTestSupport {

    @Autowired
    CodeFinder codeFinder;

    @DisplayName("코드 타입에 따라 인증 코드를 만든다.")
    @ParameterizedTest
    @EnumSource(value = CodeType.class, names = {"SIGN_UP", "PASSWORD"})
    void findCodeFrom(CodeType codeType) {
        //when
        String code = codeFinder.findCodeFrom(codeType);

        int expectedLength = switch (codeType) {
            case SIGN_UP -> 10;
            case PASSWORD -> 15;
        };

        System.out.println(code + " " + codeType);
        //then
        assertEquals(expectedLength, code.length());
    }
}