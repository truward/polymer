package com.truward.polymer.domain.analysis;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;

/**
 * @author Alexander Shabanov
 */
public final class TypeUtil {
  private TypeUtil() {} // Hidden

  @Nullable
  public static Class<?> asClass(@Nonnull DomainField field) {
    final Type type = field.getFieldType();
    return (type instanceof Class) ? (Class) type : null;
  }

  public static boolean isNullCheckRequired(@Nonnull DomainField field) {
    return field.isNullableUndecided() || field.isNullable();
  }
}
