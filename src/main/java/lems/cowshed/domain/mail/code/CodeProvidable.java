package lems.cowshed.domain.mail.code;

public interface CodeProvidable {

    boolean isSupport(CodeType codeType);
    String provide(CodeType codeType);
}
