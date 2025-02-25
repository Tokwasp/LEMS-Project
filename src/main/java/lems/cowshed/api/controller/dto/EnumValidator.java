package lems.cowshed.api.controller.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

public class EnumValidator implements ConstraintValidator<Enum, java.lang.Enum> {

    @Override
    public boolean isValid(java.lang.Enum value, ConstraintValidatorContext context) {
        if(value == null){
            return false;
        }

        Class<?> declaringClass = value.getDeclaringClass();
        return Arrays.asList(declaringClass.getEnumConstants()).contains(value);
    }
}
