package net.danshin.danshin_notice_api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TimezoneValidator.class)
@SuppressWarnings("unused")
public @interface ValidTimezone {
    String message() default "Invalid timezone. Use IANA format like 'Europe/Moscow' or 'UTC'";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
