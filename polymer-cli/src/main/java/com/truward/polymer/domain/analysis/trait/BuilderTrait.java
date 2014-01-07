package com.truward.polymer.domain.analysis.trait;

import com.truward.polymer.core.trait.Trait;
import com.truward.polymer.core.trait.TraitKey;

import javax.annotation.Nonnull;

/**
 * Trait, that designates a builder, associated with the given domain object
 *
 * @author Alexander Shabanov
 */
public final class BuilderTrait implements Trait<BuilderTrait> {
  public static final TraitKey<BuilderTrait> KEY = new Key();

  // TODO: builder settings

  @Nonnull
  @Override
  public TraitKey<BuilderTrait> getKey() {
    return KEY;
  }

  private static final class Key implements TraitKey<BuilderTrait> {
    private Key() {}

    @Nonnull
    @Override
    public Class<BuilderTrait> getTraitClass() {
      return BuilderTrait.class;
    }
  }
}
