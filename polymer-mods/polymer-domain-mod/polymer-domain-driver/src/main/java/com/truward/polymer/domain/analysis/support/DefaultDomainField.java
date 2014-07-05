package com.truward.polymer.domain.analysis.support;

import com.google.common.collect.ImmutableMap;
import com.truward.polymer.domain.analysis.DomainField;
import com.truward.polymer.domain.analysis.FieldTrait;
import com.truward.polymer.domain.analysis.OriginMethodRole;
import com.truward.polymer.freezable.FreezableSupport;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents method-based field information.
 *
 * @author Alexander Shabanov
 */
public final class DefaultDomainField extends FreezableSupport implements DomainField {
  private final String name;
  private final Type type;
  private EnumSet<FieldTrait> fieldTraits = EnumSet.noneOf(FieldTrait.class);
  private Map<OriginMethodRole, Method> originMethods = new HashMap<>();

  DefaultDomainField(@Nonnull String name, @Nonnull Type type) {
    this.name = name;
    this.type = type;

    // all primitive types are not nullable
    if (isPrimitive()) {
      putTrait(FieldTrait.NONNULL);
    }
  }

  @Nullable
  @Override
  public Class<?> getFieldTypeAsClass() {
    final Type type = getFieldType();
    if (type instanceof Class) {
      return ((Class) type);
    }

    return null;
  }

  @Nullable
  @Override
  public Method getOriginMethod(@Nonnull OriginMethodRole methodRole) {
    return originMethods.get(methodRole);
  }

  @Override
  public void putOriginMethod(@Nonnull OriginMethodRole methodRole, @Nonnull Method method) {
    checkNonFrozen();
    originMethods.put(methodRole, method);
  }

  @Override
  public boolean isPrimitive() {
    final Class<?> fieldClass = getFieldTypeAsClass();
    return fieldClass != null && fieldClass.isPrimitive();
  }

  @Override
  public boolean hasTrait(@Nonnull FieldTrait fieldTrait) {
    return fieldTraits.contains(fieldTrait);
  }

  @Override
  public void putTrait(@Nonnull FieldTrait fieldTrait) {
    checkNonFrozen();
    fieldTrait.verifyCompatibility(this);
    fieldTraits.add(fieldTrait);
  }

  @Override
  @Nonnull
  public String getFieldName() {
    return name;
  }

  @Override
  @Nonnull
  public Type getFieldType() {
    return type;
  }

  @Override
  public String toString() {
    return name + " : " + getFieldType();
  }

  @Override
  protected void beforeFreezing() {
    this.originMethods = ImmutableMap.copyOf(this.originMethods);
  }
}
