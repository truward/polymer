package com.truward.polymer.domain.analysis;

import com.truward.polymer.freezable.Freezable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Represents information about domain field.
 */
public interface DomainField extends Freezable {
  @Nonnull
  String getFieldName();

  @Nonnull
  Type getFieldType();

  @Nullable
  Class<?> getFieldTypeAsClass();

  @Nullable
  Method getOriginMethod(@Nonnull OriginMethodRole methodRole);

  void putOriginMethod(@Nonnull OriginMethodRole methodRole, @Nonnull Method method);

  boolean isPrimitive();

  boolean hasTrait(@Nonnull FieldTrait fieldTrait);

  void putTrait(@Nonnull FieldTrait fieldTrait);
}
