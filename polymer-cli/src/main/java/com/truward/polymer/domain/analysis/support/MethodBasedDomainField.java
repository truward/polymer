package com.truward.polymer.domain.analysis.support;

import com.truward.polymer.core.trait.TraitContainerSupport;
import com.truward.polymer.domain.analysis.DomainField;
import com.truward.polymer.domain.analysis.trait.SimpleDomainFieldTrait;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Represents method-based field information.
 *
 * @author Alexander Shabanov
 */
public final class MethodBasedDomainField extends TraitContainerSupport implements DomainField {
  private final String name;
  private final Method originMethod;
  private final String getterName;

  MethodBasedDomainField(@Nonnull String name, @Nonnull Method originMethod) {
    this.name = name;
    this.originMethod = originMethod;
    this.getterName = NamingUtil.createGetterName(getFieldType(), getFieldName());

    // all primitive types are not nullable
    if (getFieldType() instanceof Class && ((Class) getFieldType()).isPrimitive()) {
      putTrait(SimpleDomainFieldTrait.NONNULL);
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

  @Override
  public String toString() {
    return name + " : " + getFieldType();
  }
}
