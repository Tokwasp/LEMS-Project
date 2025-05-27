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
@NotBlank(message = "회원 비밀번호는 필수 입니다.")
@Pattern(
        regexp = "^(?=(?:[^!@#$%^&*()]*[!@#$%^&*()]){2,})[A-Za-z0-9!@#$%^&*()]{8,15}$",
        message = "비밀번호는 최소 8자 ~ 15자이며 특수문자를 2개 이상 포함해야 합니다."
)
public @interface PasswordCheck {
    String message() default "잘못된 비밀번호 형식 입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
