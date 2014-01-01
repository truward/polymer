package com.truward.polymer.domain.analysis;

import com.google.common.collect.ImmutableSet;
import com.truward.polymer.domain.analysis.trait.SimpleDomainFieldTrait;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * @author Alexander Shabanov
 */
public final class TypeUtil {
  private TypeUtil() {} // Hidden

  public static Set<Class<?>> NUMERIC_PRIMITIVES = ImmutableSet.<Class<?>>of(int.class, char.class, byte.class,
      long.class, double.class, float.class);

  @Nullable
  public static Class<?> asClass(@Nonnull DomainField field) {
    final Type type = field.getFieldType();
    return (type instanceof Class) ? (Class) type : null;
  }

  public static boolean isNullCheckRequired(@Nonnull DomainField field) {
    return field.hasTrait(SimpleDomainFieldTrait.NONNULL) || !field.hasTrait(SimpleDomainFieldTrait.NULLABLE);
  }
}
