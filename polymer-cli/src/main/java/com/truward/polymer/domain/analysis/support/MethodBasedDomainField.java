package com.truward.polymer.domain.analysis.support;

import com.truward.polymer.core.typesystem.NamingUtil;
import com.truward.polymer.domain.analysis.DomainField;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Represents method-based field information.
 *
 * @author Alexander Shabanov
 */
public final class MethodBasedDomainField implements DomainField {
  private final String name;
  private final Method originMethod;
  private final String getterName;
  private Boolean nullable;

  MethodBasedDomainField(@Nonnull String name, @Nonnull Method originMethod) {
    this.name = name;
    this.originMethod = originMethod;
    this.getterName = NamingUtil.createGetterName(getFieldType(), getFieldName());

    // all primitive types are not nullable
    if (getFieldType() instanceof Class && ((Class) getFieldType()).isPrimitive()) {
      setNullable(false);
    }
  }

  @Override
  @Nonnull
  public String getFieldName() {
    return name;
  }

  @Override
  @Nonnull
  public String getGetterName() {
    return getterName;
  }

  @Override
  @Nonnull
  public Type getFieldType() {
    return originMethod.getGenericReturnType();
  }

  @Nullable
  @Override
  public Class<?> getFieldTypeAsClass() {
    final Type fieldType = getFieldType();
    return fieldType instanceof Class ? ((Class) fieldType) : null;
  }

  @Override
  public boolean isNullable() {
    if (this.nullable == null) {
      throw new IllegalStateException("Nullable value is not known");
    }
    return nullable;
  }

  @Override
  public boolean isNullableUndecided() {
    return nullable == null;
  }

  @Override
  public void setNullable(boolean nullable) {
    if (this.nullable != null) {
      throw new IllegalStateException("Unable to assign nullable value twice");
    }
    this.nullable = nullable;
  }

  @Override
  public String toString() {
    return name + " : " + getFieldType();
  }
}
