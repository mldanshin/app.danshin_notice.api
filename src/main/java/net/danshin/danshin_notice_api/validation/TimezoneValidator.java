package net.danshin.danshin_notice_api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.DateTimeException;
import java.time.ZoneId;

@SuppressWarnings("unused")
public class TimezoneValidator implements ConstraintValidator<ValidTimezone, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }

        try {
            ZoneId zoneId = ZoneId.of(value);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }
}
