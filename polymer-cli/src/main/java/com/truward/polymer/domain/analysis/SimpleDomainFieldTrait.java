package com.truward.polymer.domain.analysis;

import com.truward.polymer.core.trait.Trait;
import com.truward.polymer.core.trait.TraitKey;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public enum SimpleDomainFieldTrait implements Trait<SimpleDomainFieldTrait>, TraitKey<SimpleDomainFieldTrait> {

  /**
   * Designates a nullable field, can not be applied to the primitive types.
   */
  NULLABLE,

  /**
   * Designates a non-nullable field
   */
  NONNULL,

  /**
   * Designates a mutable object, can be applied to interface types which descendants may support modification, e.g.
   * if applied given a List element the domain driver might pick <code>ArrayList</code> if mutable trait is assigned
   * or guava's <code>ImmutableList</code> if not.
   */
  MUTABLE,

  /**
   * Designates a given field to be a non-negative one.
   * For numbers only.
   */
  NON_NEGATIVE,

  /**
   * Designates a given string to be
   */
  HAS_LENGTH;

  @Nonnull
  @Override
  public SimpleDomainFieldTrait getKey() {
    return this;
  }


  @Nonnull
  @Override
  public Class<SimpleDomainFieldTrait> getTraitClass() {
    return SimpleDomainFieldTrait.class;
  }
}
