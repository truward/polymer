package com.truward.polymer.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Designates specification method, this annotation is similar to junit's <code>@Test</code> annotation.
 *
 * @author Alexander Shabanov
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Specification {
  int ordinal() default Integer.MAX_VALUE;
}
