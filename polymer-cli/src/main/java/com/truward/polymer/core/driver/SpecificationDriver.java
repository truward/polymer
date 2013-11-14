package com.truward.polymer.core.driver;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public interface SpecificationDriver {
  @Nonnull
  List<Class<?>> getProvidedResourceClasses();

  @Nonnull
  Object provide(@Nonnull Class<?> clazz);

  void setState(@Nonnull SpecificationState state);
}
