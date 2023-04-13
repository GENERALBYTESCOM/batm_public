package com.generalbytes.batm.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to specify input validation on fields and parameters.
 * It allows developers to define a method for input validation at runtime.
 * The validation method should be implemented separately and should return a boolean value
 * indicating if the input is valid (true) or not (false).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface InputValidation {
    String method() default "";
}
