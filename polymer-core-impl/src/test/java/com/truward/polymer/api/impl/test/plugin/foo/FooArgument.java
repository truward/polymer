package com.truward.polymer.api.impl.test.plugin.foo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Foo plugin-specific annotation.
 *
 * @author Alexander Shabanov
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface FooArgument {
}
