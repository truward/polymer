package com.truward.polymer.code;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public abstract class TypeVisitor<R> {
  public R visitType(@Nonnull Type sourceType) {
    throw new UnsupportedOperationException("Unsupported type=" + sourceType);
  }

  public R visitArray(@Nonnull Type sourceType, @Nonnull Class<?> elementType) {
    return visitType(sourceType);
  }

  public R visitGenericType(@Nonnull Type sourceType, @Nonnull Class<?> rawType, @Nonnull List<Type> args) {
    return visitType(sourceType);
  }

  public R visitClass(@Nonnull Type sourceType, @Nonnull Class<?> clazz) {
    return visitType(sourceType);
  }

  public static <R> R apply(@Nonnull TypeVisitor<R> visitor, @Nonnull Type sourceType) {
    if (sourceType instanceof Class) {
      final Class<?> clazz = (Class<?>) sourceType;
      if (clazz.isArray()) {
        return visitor.visitArray(sourceType, clazz.getComponentType());
      } else {
        return visitor.visitClass(sourceType, clazz);
      }
    } else if (sourceType instanceof ParameterizedType) {
      final ParameterizedType parameterizedType = (ParameterizedType) sourceType;
      final Type rawType = parameterizedType.getRawType();
      if (rawType instanceof Class) {
        return visitor.visitGenericType(sourceType, (Class<?>) rawType,
            ImmutableList.copyOf(parameterizedType.getActualTypeArguments()));
      } else {
        return visitor.visitType(sourceType);
      }
    } else {
      return visitor.visitType(sourceType);
    }
  }
}
