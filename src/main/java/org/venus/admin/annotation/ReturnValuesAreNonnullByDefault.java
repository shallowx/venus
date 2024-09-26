package org.venus.admin.annotation;

import java.lang.annotation.*;

/**
 * This annotation indicates that the return values of all methods in the annotated package
 * are non-null by default. This helps in reducing the need to explicitly annotate every method
 * with @Nonnull and enhances code readability and consistency.
 *
 * The annotation should be applied at the package level.
 *
 * The retention policy is SOURCE, meaning the annotation is discarded by the compiler
 * and not included in the compiled bytecode.
 *
 * The @Documented annotation ensures that this annotation is included in the Javadoc.
 */
@Documented
@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.SOURCE)
public @interface ReturnValuesAreNonnullByDefault {
}
