package com.truward.polymer.core.util;

import com.truward.polymer.core.trait.Trait;
import com.truward.polymer.core.trait.TraitKey;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;

/**
 * Trait, that identifies implementation target, that corresponds to certain entity.
 *
 * @author Alexander Shabanov
 */
public final class TargetTrait implements Trait<TargetTrait> {
  public static final TraitKey<TargetTrait> KEY = new Key();

  private FqName targetName;

  public TargetTrait() {
  }

  @Nonnull
  public FqName getTargetName() {
    if (targetName == null) {
      throw new IllegalStateException("Can't get target name: not initialized yet");
    }
    return targetName;
  }

  public TargetTrait setTargetName(@Nonnull FqName targetName) {
    if (this.targetName != null) {
      throw new IllegalStateException("Target name has been initialized already");
    }
    this.targetName = targetName;
    return this;
  }

  @Nonnull
  @Override
  public TraitKey<TargetTrait> getKey() {
    return KEY;
  }

  private static final class Key implements TraitKey<TargetTrait> {
    private Key() {}

    @Nonnull
    @Override
    public Class<TargetTrait> getTraitClass() {
      return TargetTrait.class;
    }
  }
}
