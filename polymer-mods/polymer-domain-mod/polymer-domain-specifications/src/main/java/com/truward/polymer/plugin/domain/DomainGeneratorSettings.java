package com.truward.polymer.plugin.domain;

import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;

/**
 * Global settings for the code, generated for all the classes.
 *
 * @author Alexander Shabanov
 */
public interface DomainGeneratorSettings {

  @Nonnull
  DomainGeneratorSettings setDefensiveCopyStyle(@Nonnull DefensiveCopyStyle defensiveCopyStyle);

  @Nonnull
  DomainGeneratorSettings registerValidationInterfaceMarker(@Nonnull Class<?> validationInterface);

  @Nonnull
  DomainGeneratorSettings registerFreezableInterfaceMarker(@Nonnull Class<?> validationInterface);

  @Nonnull
  DomainGeneratorSettings setDefaultTargetPackage(@Nonnull FqName defaultTargetPackage);

  @Nonnull
  DomainGeneratorSettings setDefaultGeneratedClassPrefix(@Nonnull String prefix);

  @Nonnull
  DomainGeneratorSettings setDefaultGeneratedClassSuffix(@Nonnull String suffix);
}
