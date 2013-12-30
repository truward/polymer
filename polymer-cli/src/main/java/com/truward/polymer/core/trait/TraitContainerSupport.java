package com.truward.polymer.core.trait;

import com.google.common.collect.ImmutableMap;
import com.truward.polymer.code.freezable.Freezable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Shabanov
 */
public abstract class TraitContainerSupport implements TraitContainer, Freezable {
  private Map<TraitKey<?>, Trait> traitMap = new HashMap<>();

  @Nullable
  @Override
  public <T extends Trait> T findTrait(@Nonnull TraitKey<T> key) {
    final Trait result = traitMap.get(key);
    assert result.getKey().equals(key);
    return key.getTraitClass().cast(result);
  }

  @Override
  public boolean hasTrait(@Nonnull TraitKey<?> key) {
    return traitMap.containsKey(key);
  }

  @Nullable
  @Override
  public <T extends Trait> Trait putTrait(@Nonnull T trait) {
    return traitMap.put(trait.getKey(), trait);
  }

  @Override
  public void freeze() {
    traitMap = ImmutableMap.copyOf(traitMap);
  }
}
