package com.truward.polymer.core.trait;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Alexander Shabanov
 */
public interface TraitContainer {
  @Nullable
  <T extends Trait> T findTrait(@Nonnull TraitKey<T> key);

  boolean hasTrait(@Nonnull TraitKey<?> key);

  @Nullable
  <T extends Trait> Trait putTrait(@Nonnull T trait);
}
