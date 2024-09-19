package org.venus.admin.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A validator class to check if a given date string represents a date-time in the future.
 *
 * This class implements the ConstraintValidator interface to provide validation logic for
 * the FutureDate annotation. The date string must be in the format "yyyy-MM-dd HH:mm:ss".
 */
public class FutureDateValidator implements ConstraintValidator<FutureDate, String> {
    /**
     * Validates whether the given date string is in the future.
     *
     * @param date the date string to validate, expected to be in the format "yyyy-MM-dd HH:mm:ss"
     * @param context the context in which the constraint is evaluated
     * @return true if the date is in the future, false otherwise
     */
    @Override
    public boolean isValid(String date, ConstraintValidatorContext context) {
        if (date == null) {
            return false;
        }
        LocalDateTime dateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return dateTime.isAfter(LocalDateTime.now());
    }
}
