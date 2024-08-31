package org.venus.admin.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FutureDateValidator implements ConstraintValidator<FutureDate, String> {
    @Override
    public boolean isValid(String date, ConstraintValidatorContext context) {
        if (date == null) {
            return false;
        }
        LocalDateTime dateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return dateTime.isAfter(LocalDateTime.now());
    }
}
