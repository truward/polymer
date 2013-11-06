package com.truward.polymer.domain.analysis;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;

/**
 * Represents information about domain field.
 */
public interface DomainField {
  @Nonnull
  String getFieldName();

  @Nonnull
  String getGetterName();

  @Nonnull
  Type getFieldType();

  @Nullable
  Class<?> getFieldTypeAsClass();

  /**
   * Determines whether the given property is nullable or not.
   * The only way the object can not be null is only when in all the field assignment it is
   * verified that the provided value is not null and an exception is thrown otherwise.
   * Throws {@link IllegalStateException} if nullability state is not known (undecided).
   * @see #isNullableUndecided() for verification of the nullability state.
   *
   * @return True, if object value can be null
   */
  boolean isNullable();

  /**
   * @return True if it is not known whether the particular field is nullable or not
   */
  boolean isNullableUndecided();

  /**
   * Sets nullability property. This can be invoked only once.
   * Removes nullability undecided state.
   * @see #isNullableUndecided()
   *
   * @param nullable Nullable state
   */
  void setNullable(boolean nullable);
}
