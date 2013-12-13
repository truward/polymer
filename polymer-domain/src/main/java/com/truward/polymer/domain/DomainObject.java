package com.truward.polymer.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Designates domain object parameter, identical for
 * <code>
 *   // ...
 *   Foo instance = specification.domainObject(Class&lt;Foo&gt; klass)
 * </code>
 *
 * @author Alexander Shabanov
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface DomainObject {
}
