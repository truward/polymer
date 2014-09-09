package com.truward.polymer.api.plugin;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public interface SpecificationStateAware {

  void setState(@Nonnull SpecificationState state);
}
