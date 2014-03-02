package com.truward.polymer.domain.analysis;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;

/**
 * @author Alexander Shabanov
 */
public final class FieldUtil {
  /** Hidden */
  private FieldUtil() {}

  @Nullable
  public static String getMethodName(@Nonnull DomainField field, @Nonnull OriginMethodRole methodRole) {
    final Method method = field.getOriginMethod(methodRole);
    return method != null ? method.getName() : null;
  }

  public static boolean isNullCheckRequired(@Nonnull DomainField field) {
    return field.hasTrait(FieldTrait.IMMUTABLE) || !field.hasTrait(FieldTrait.MUTABLE);
  }
}
