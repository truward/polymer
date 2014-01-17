package com.truward.polymer.domain.analysis.trait;

import com.truward.polymer.core.trait.Trait;
import com.truward.polymer.core.trait.TraitKey;
import com.truward.polymer.domain.DomainObjectBuilderSettings;

import javax.annotation.Nonnull;

/**
 * Trait, that designates a builder, associated with the given domain object
 *
 * @author Alexander Shabanov
 */
public final class BuilderTrait implements Trait<BuilderTrait> {
  public static final TraitKey<BuilderTrait> KEY = new Key();

  private final Settings settings = new Settings();

  @Nonnull
  @Override
  public TraitKey<BuilderTrait> getKey() {
    return KEY;
  }

  @Nonnull
  public DomainObjectBuilderSettings getSettings() {
    return settings;
  }

  private static final class Key implements TraitKey<BuilderTrait> {
    private Key() {}

    @Nonnull
    @Override
    public Class<BuilderTrait> getTraitClass() {
      return BuilderTrait.class;
    }
  }

  private static final class Settings implements DomainObjectBuilderSettings {
  }
}
