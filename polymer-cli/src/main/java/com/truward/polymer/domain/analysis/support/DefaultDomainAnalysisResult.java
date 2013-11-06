package com.truward.polymer.domain.analysis.support;

import com.google.common.collect.ImmutableList;
import com.truward.polymer.core.typesystem.NamingUtil;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.DomainField;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public final class DefaultDomainAnalysisResult implements DomainAnalysisResult {
  private final Class<?> originClass;
  private final List<DomainField> declaredFields;

  public DefaultDomainAnalysisResult(@Nonnull Class<?> clazz) {
    originClass = clazz;

    final List<DomainField> fields = new ArrayList<>();

    //final Type[] gts = clazz.getGenericInterfaces();
    //final Method[] declaredMethods = clazz.getDeclaredMethods();
    for (final Method method : clazz.getDeclaredMethods()) {
      fields.add(inferField(method));
    }

    this.declaredFields = ImmutableList.copyOf(fields);
  }

  @Override
  @Nonnull
  public Class<?> getOriginClass() {
    return originClass;
  }

  @Override
  @Nonnull
  public Collection<? extends DomainField> getDeclaredFields() {
    return declaredFields;
  }

  //
  // Private
  //

  private DomainField inferField(Method method) {
    if (method.getParameterTypes().length > 0) {
      throw new RuntimeException("Unsupported method " + method + "with parameters in the domain object");
    }

    final String fieldName = NamingUtil.asFieldName(method.getName());

    final DomainField field = new MethodBasedDomainField(fieldName, method);

    // TODO: infer nullability
    if (field.isNullableUndecided()) {
      field.setNullable(false);
    }

    return field;
  }
}
