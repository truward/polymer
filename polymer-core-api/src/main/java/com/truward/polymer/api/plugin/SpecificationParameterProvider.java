package com.truward.polymer.api.plugin;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

/**
 * Interface to plugin parameter provider. The corresponding implementation may provide the corresponding parameter,
 * injected into the corresponding method parameter by the specification processor.
 *
 * @author Alexander Shabanov
 */
public interface SpecificationParameterProvider {

  <R> boolean canProvideParameter(@Nonnull List<? extends Annotation> annotations,
                                  @Nonnull Class<R> resultType);

  @Nonnull
  <R> R provideParameter(@Nonnull List<? extends Annotation> annotations,
                         @Nonnull Class<R> resultType);
}
