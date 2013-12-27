package com.truward.polymer.core.trait;

import javax.annotation.Nonnull;

/**
 * Represents an arbitrary trait of certain entity, e.g. domain field constraint - nonnull, etc.
 *
 * @author Alexander Shabanov
 */
public interface Trait<TSelf extends Trait> {
  @Nonnull
  TraitKey<TSelf> getKey();
}
