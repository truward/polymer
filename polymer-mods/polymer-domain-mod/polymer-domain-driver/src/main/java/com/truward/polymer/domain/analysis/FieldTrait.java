package com.truward.polymer.domain.analysis;

import com.truward.polymer.core.types.DefaultValues;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public enum FieldTrait {
  /**
   * Designates a nullable field, can not be applied to the primitive types.
   */
  NULLABLE {
    @Override
    public void verifyCompatibility(@Nonnull DomainField field) {
      incompatibleWith(field, NONNULL);
    }
  },

  /**
   * Designates a non-nullable field.
   */
  NONNULL {
    @Override
    public void verifyCompatibility(@Nonnull DomainField field) {
      incompatibleWith(field, NULLABLE);
    }
  },

  /**
   * Designates a mutable object, can be applied to interface types which descendants may support modification, e.g.
   * if applied given a List element the domain driver might pick <code>ArrayList</code> if mutable trait is assigned
   * or guava's <code>ImmutableList</code> if not.
   */
  MUTABLE {
    @Override
    public void verifyCompatibility(@Nonnull DomainField field) {
      incompatibleWith(field, IMMUTABLE);
    }
  },

  /**
   * Explicitly designates an object to be immutable. All the attempts to specify setters to mutate object will result in
   * error.
   */
  IMMUTABLE {
    @Override
    public void verifyCompatibility(@Nonnull DomainField field) {
      incompatibleWith(field, MUTABLE);
    }
  },

  /**
   * Designates a given field to be a non-negative one.
   * For numbers only.
   */
  NON_NEGATIVE {

    @Override
    public void verifyCompatibility(@Nonnull DomainField field) {
      final Class<?> fieldClass = field.getFieldTypeAsClass();
      if (fieldClass == null ||
          (!Number.class.isAssignableFrom(fieldClass) && !DefaultValues.NUMERIC_PRIMITIVES.contains(fieldClass))) {
        throw new RuntimeException("Only numeric fields can be associated with NON_NEGATIVE trait");
      }
    }
  },

  /**
   * Designates a given string to both non-nullable and non-empty (i.e. to have length greater than zero)
   */
  HAS_LENGTH {
    @Override
    public void verifyCompatibility(@Nonnull DomainField field) {
      if (!String.class.equals(field.getFieldType())) {
        throw new RuntimeException("Only string fields can be associated with HAS_LENGTH trait");
      }

      incompatibleWith(field, NULLABLE);
    }
  };

  //
  // Impl
  //

  /**
   * Verifies, whether the trait is compatible with the existing one in the given trait container
   *
   * @param field Field
   */
  public void verifyCompatibility(@Nonnull DomainField field) {
  }

  public final void incompatibleWith(@Nonnull DomainField field, @Nonnull FieldTrait other) {
    if (field.hasTrait(other)) {
      throw new IllegalStateException("Field " + field + " is incompatible with trait " + this.name() +
          " as it already has trait " + other.name());
    }
  }
}
