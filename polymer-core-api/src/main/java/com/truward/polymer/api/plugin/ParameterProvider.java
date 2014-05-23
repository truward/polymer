package com.truward.polymer.api.plugin;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public interface ParameterProvider {

  <R> boolean canProvideParameter(@Nonnull Collection<? extends Annotation> annotations,
                                  @Nonnull Class<R> resultType);

  @Nonnull
  <R> R provideParameter(@Nonnull Collection<? extends Annotation> annotations,
                         @Nonnull Class<R> resultType);
}
