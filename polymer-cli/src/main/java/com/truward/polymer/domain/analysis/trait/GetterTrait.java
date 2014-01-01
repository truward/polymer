package com.truward.polymer.domain.analysis.trait;

import com.truward.polymer.core.trait.Trait;
import com.truward.polymer.core.trait.TraitKey;

import javax.annotation.Nonnull;

/**
 * Trait that designates getter method with the associated domain object.
 *
 * @author Alexander Shabanov
 */
public final class GetterTrait implements Trait<GetterTrait> {

  public static final TraitKey<GetterTrait> KEY = new Key();
  private final String getterName;

  public GetterTrait(@Nonnull String getterName) {
    this.getterName = getterName;
  }

  @Nonnull
  public String getGetterName() {
    return getterName;
  }

  @Nonnull
  @Override
  public TraitKey<GetterTrait> getKey() {
    return KEY;
  }

  private static final class Key implements TraitKey<GetterTrait> {
    private Key() {}

    @Nonnull
    @Override
    public Class<GetterTrait> getTraitClass() {
      return GetterTrait.class;
    }
  }
}
