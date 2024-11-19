package lems.cowshed.domain.user;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = CustomSecurityContextFactory.class)
public @interface WithMockCustomUser {
    long id() default 1L;
    String email() default "test@naver.com";
    String username() default "테스트";
    String password() default "test";
}
