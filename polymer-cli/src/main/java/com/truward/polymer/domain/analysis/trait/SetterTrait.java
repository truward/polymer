package com.truward.polymer.domain.analysis.trait;

import com.truward.polymer.core.trait.Trait;
import com.truward.polymer.core.trait.TraitKey;

import javax.annotation.Nonnull;

/**
 * Trait that designates a setter associated with the particular domain field.
 *
 * @author Alexander Shabanov
 */
public final class SetterTrait implements Trait<SetterTrait> {
  public static final TraitKey<SetterTrait> KEY = new Key();
  private final String setterName;

  public SetterTrait(@Nonnull String setterName) {
    this.setterName = setterName;
  }

  @Nonnull
  public String getSetterName() {
    return setterName;
  }

  @Nonnull
  @Override
  public TraitKey<SetterTrait> getKey() {
    return KEY;
  }

  private static final class Key implements TraitKey<SetterTrait> {
    private Key() {}

    @Nonnull
    @Override
    public Class<SetterTrait> getTraitClass() {
      return SetterTrait.class;
    }
  }
}
