package org.venus.admin.annotations;

import java.lang.annotation.*;

/**
 * The ParametersAreNonnullByDefault annotation is used to indicate that the parameters of methods
 * in the annotated package are non-null by default, unless there is:
 * 1. An explicit nullness annotation (such as @Nullable) present on a parameter.
 * 2. An overriding annotation applied at the class or method level within the package.
 *
 * By applying this annotation at the package level, it helps in reducing the redundancy of
 * specifying @Nonnull annotations on each parameter, thus making the code more readable
 * and reducing the likelihood of null-related errors.
 **/
@Documented
@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.SOURCE)
public @interface ParametersAreNonnullByDefault {
}
