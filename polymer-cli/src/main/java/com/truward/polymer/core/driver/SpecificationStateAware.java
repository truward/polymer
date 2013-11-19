package com.truward.polymer.core.driver;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public interface SpecificationStateAware {
  void setState(@Nonnull SpecificationState state);
}
