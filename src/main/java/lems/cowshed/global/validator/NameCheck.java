package lems.cowshed.global.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@NotBlank(message = "유저 닉네임은 필수 입니다.")
@Pattern(
        regexp = "^[A-Za-z가-힣0-9]{2,10}$",
        message = "회원의 이름은 한글과 영어 문자만 가능하며 최대 10자리 입니다."
)
public @interface NameCheck {
    String message() default "잘못된 이름 형식 입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
