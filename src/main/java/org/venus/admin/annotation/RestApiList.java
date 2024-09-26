package org.venus.admin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify that a method or parameter is associated with a REST API list.
 * It can be applied to methods and parameters to provide additional metadata or behavior
 * for handling list-type data in REST API interactions.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RestApiList {
    // only just tips
}
