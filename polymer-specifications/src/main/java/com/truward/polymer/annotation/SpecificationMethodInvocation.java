package com.truward.polymer.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies, that current parameter is not an object, but a function call (required for compiler checks)
 *
 * @author Alexander Shabanov
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.PARAMETER})
public @interface SpecificationMethodInvocation {
}
