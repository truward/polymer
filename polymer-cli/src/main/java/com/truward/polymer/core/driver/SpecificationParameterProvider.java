package com.truward.polymer.core.driver;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public interface SpecificationParameterProvider {

  <T extends Annotation, R> boolean canProvideParameter(@Nonnull List<T> annotations, @Nonnull Class<R> resultType);

  <T extends Annotation, R> R provideParameter(@Nonnull List<T> annotations, @Nonnull Class<R> resultType);
}
