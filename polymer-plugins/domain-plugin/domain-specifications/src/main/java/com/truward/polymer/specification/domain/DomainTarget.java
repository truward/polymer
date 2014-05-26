package com.truward.polymer.specification.domain;

import com.truward.polymer.specification.SpecificationTarget;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public interface DomainTarget<T> extends SpecificationTarget<T> {

  @Nonnull
  DomainTarget<T> generateInnerBuilder();

  @Nonnull
  DomainTarget<T> generateDefaultPublicConstructor();

  @Nonnull
  DomainTarget<T> generateSetters();

  @Nonnull
  DomainTarget<T> makeFreezable();

  @Nonnull
  DomainTarget<T> implementFreezableInterface(@Nonnull Class<?> freezableInterface);

  @Nonnull
  DomainTarget<T> generateValidationMethod();

  @Nonnull
  DomainTarget<T> implementValidationInterface(@Nonnull Class<?> validationInterface);
}
