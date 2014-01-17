package com.truward.polymer.domain.analysis.trait;

import com.truward.polymer.core.trait.Trait;
import com.truward.polymer.core.trait.TraitKey;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public final class ImplementationNameTrait implements Trait<ImplementationNameTrait> {
  public static final TraitKey<ImplementationNameTrait> KEY = new Key();
  private final String implementationName;

  public ImplementationNameTrait(@Nonnull String implementationName) {
    this.implementationName = implementationName;
  }

  @Nonnull
  public String getImplementationName() {
    return implementationName;
  }

  @Nonnull
  @Override
  public TraitKey<ImplementationNameTrait> getKey() {
    return KEY;
  }

  private static final class Key implements TraitKey<ImplementationNameTrait> {
    private Key() {}

    @Nonnull
    @Override
    public Class<ImplementationNameTrait> getTraitClass() {
      return ImplementationNameTrait.class;
    }
  }
}
