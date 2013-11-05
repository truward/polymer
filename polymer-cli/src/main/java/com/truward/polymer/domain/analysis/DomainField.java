package com.truward.polymer.domain.analysis;

import java.lang.reflect.Type;

/**
 * Represents information about domain field.
 */
public interface DomainField {
  String getFieldName();

  String getGetterName();

  Type getFieldType();

  /**
   * Identifies whether the generated code can assume that the given property can be nullable.
   * The only way the object can not be null is only when in all the field assignment it is
   * verified that the provided value is not null and an exception is thrown otherwise.
   *
   * @return True, if object value can be null
   */
  boolean isNullable();

  /**
   * @return True if it is not known whether the particular field is nullable or not
   */
  boolean isNullableUndecided();

  void setNullable(boolean nullable);
}
