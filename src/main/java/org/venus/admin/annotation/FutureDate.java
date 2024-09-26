package org.venus.admin.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A constraint annotation to validate that a given date is in the future.
 *
 * This annotation can be applied to fields or parameters of type String.
 * The annotated element must represent a date-time string in the format "yyyy-MM-dd HH:mm:ss".
 *
 * The constraint is validated using the FutureDateValidator class.
 *
 * @return the default message if the date is not in the future
 * @return the groups the constraint belongs to
 * @return the payload associated with the constraint
 */
@Constraint(validatedBy = FutureDateValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureDate {
    /**
     * The default validation message if the date is not in the future.
     *
     * @return the default message
     */
    String message() default "The date must be in the future";

    /**
     * Specifies the groups the constraint belongs to.
     *
     * @return an array of Class objects representing the groups the constraint belongs to
     */
    Class<?>[] groups() default {};

    /**
     * Specifies the payload associated with the constraint.
     *
     * This can be used by clients of the validation framework to assign custom
     * payload objects to a constraint.
     *
     * @return the payload associated with the constraint
     */
    Class<? extends Payload>[] payload() default {};
}
