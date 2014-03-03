package com.truward.polymer.core.code.typed;

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

  public R visitGenericType(@Nonnull Type sourceType, @Nonnull Type rawType, @Nonnull List<Type> args) {
    return visitType(sourceType);
  }

  public R visitClass(@Nonnull Type sourceType, @Nonnull Class<?> clazz) {
    return visitType(sourceType);
  }

  public R visitGenClass(@Nonnull Type sourceType, @Nonnull GenClass genClass) {
    return visitType(sourceType);
  }

  public R visitGenArray(@Nonnull Type sourceType, @Nonnull GenArray genArray) {
    return visitType(sourceType);
  }

  public R visitGenParameterizedType(@Nonnull Type sourceType, @Nonnull GenParameterizedType genParameterizedType) {
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
      return visitor.visitGenericType(sourceType, rawType,
          ImmutableList.copyOf(parameterizedType.getActualTypeArguments()));
    } else if (sourceType instanceof GenClass) {
      return visitor.visitGenClass(sourceType, (GenClass) sourceType);
    } else if (sourceType instanceof GenArray) {
      return visitor.visitGenArray(sourceType, (GenArray) sourceType);
    } else if (sourceType instanceof GenParameterizedType) {
      return visitor.visitGenParameterizedType(sourceType, (GenParameterizedType) sourceType);
    } else {
      return visitor.visitType(sourceType);
    }
  }
}
