package com.truward.polymer.domain.analysis;

import com.google.common.collect.ImmutableSet;
import com.truward.polymer.core.trait.Trait;
import com.truward.polymer.core.trait.TraitContainer;
import com.truward.polymer.core.trait.TraitKey;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * @author Alexander Shabanov
 */
public enum SimpleDomainFieldTrait implements Trait<SimpleDomainFieldTrait>, TraitKey<SimpleDomainFieldTrait> {

  /**
   * Designates a nullable field, can not be applied to the primitive types.
   */
  NULLABLE {
    @Override
    public void verifyCompatibility(@Nonnull DomainField field) {
      if (field.hasTrait(NONNULL)) {
        throw new RuntimeException("Nullable trait can not be assigned to a field with the existing nonnull trait");
      }
    }
  },

  /**
   * Designates a non-nullable field
   */
  NONNULL {
    @Override
    public void verifyCompatibility(@Nonnull DomainField field) {
      if (field.hasTrait(NULLABLE)) {
        throw new RuntimeException("Nonnull trait can not be assigned to a field with the existing nullable trait");
      }
    }
  },

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
  NON_NEGATIVE {

    @Override
    public void verifyCompatibility(@Nonnull DomainField field) {
      final Type t = field.getFieldType();
      do {
        if (!(t instanceof Class)) {
          break;
        }

        final Class<?> c = (Class<?>) t;
        if (!Number.class.isAssignableFrom(c) && !TypeUtil.NUMERIC_PRIMITIVES.contains(c)) {
          break;
        }

        return; // ok
      } while (false);

      throw new RuntimeException("Only numeric fields can be associated with NON_NEGATIVE trait");
    }
  },

  /**
   * Designates a given string to be
   */
  HAS_LENGTH {
    @Override
    public void verifyCompatibility(@Nonnull DomainField field) {
      if (!String.class.equals(field.getFieldType())) {
        throw new RuntimeException("Only string fields can be associated with HAS_LENGTH trait");
      }
    }
  };

  //
  // Impl
  //

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

  /**
   * Verifies, whether the trait is compatible with the existing one in the given trait container
   *
   * @param field Field
   */
  public void verifyCompatibility(@Nonnull DomainField field) {
  }
}
