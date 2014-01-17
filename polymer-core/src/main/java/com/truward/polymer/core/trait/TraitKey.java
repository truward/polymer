package com.truward.polymer.core.trait;

import javax.annotation.Nonnull;

/**
 * Represents a key by using which certain trait can be retrieved.
 *
 * @author Alexander Shabanov
 */
public interface TraitKey<T extends Trait> {
  @Nonnull
  Class<T> getTraitClass();
}
