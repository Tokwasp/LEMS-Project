package lems.cowshed.global.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@NotBlank(message = "이메일 값은 필수 입니다.")
@Pattern(
        regexp = "^[A-Za-z0-9]+@[A-Za-z0-9]+\\.com$",
        message = "올바른 이메일 형식이어야 합니다."
)
@Size(max = 30, message = "이메일은 최대 30자까지 입력 가능 합니다.")
public @interface EmailCheck {
    String message() default "잘못된 이메일 형식 입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
