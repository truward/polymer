package com.truward.polymer.annotation;

import java.lang.annotation.*;

/**
 * @author Alexander Shabanov
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ProvidedService {
  String value() default "";

  String targetPackage();

  DtoClassTrait[] dtoClassTraits() default {};

  String[] options() default {};

  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target({})
  @interface DtoClassTrait {
    Class<?> target();
    Class<?> extended() default Object.class;
    Class<?>[] implemented() default {};
  }
}
