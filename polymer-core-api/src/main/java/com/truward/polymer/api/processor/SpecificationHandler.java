package com.truward.polymer.api.processor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Handler of the specification classes, that maintains inner structure of the domain class.
 *
 * @author Alexander Shabanov
 */
public interface SpecificationHandler {

  /**
   * Instantiates methods in the given class and parses specification methods taking into an account
   * specification ordinals.
   *
   * @param clazz Specification definition class.
   * @return Processed instance of the specification definition class.
   */
  @Nullable
  <T> T parseClass(@Nonnull Class<T> clazz);

  void done();
}
