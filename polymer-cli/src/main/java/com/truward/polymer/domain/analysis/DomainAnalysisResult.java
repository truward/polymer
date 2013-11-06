package com.truward.polymer.domain.analysis;

import com.google.common.collect.ImmutableList;
import com.truward.polymer.core.typesystem.NamingUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Tries to reinterpret incoming class as a domain object, extracts information about its
 * fields and field names.
 *
 * @author Alexander Shabanov
 */
public class DomainAnalysisResult {

  private final Class<?> originClass;
  private final List<DomainField> declaredFields;

  public DomainAnalysisResult(@Nonnull Class<?> clazz) {
    originClass = clazz;

    final List<DomainField> fields = new ArrayList<>();

    //final Type[] gts = clazz.getGenericInterfaces();
    //final Method[] declaredMethods = clazz.getDeclaredMethods();
    for (final Method method : clazz.getDeclaredMethods()) {
      fields.add(inferField(method));
    }

    this.declaredFields = ImmutableList.copyOf(fields);
  }

  @Nonnull
  public Class<?> getOriginClass() {
    return originClass;
  }

  @Nonnull
  public Collection<? extends DomainField> getDeclaredFields() {
    return declaredFields;
  }

  /**
   * Represents method-based field information.
   */
  public static final class MethodBasedDomainField implements DomainField {
    private final String name;
    private final Method originMethod;
    private final String getterName;
    private Boolean nullable;

    private MethodBasedDomainField(@Nonnull String name, @Nonnull Method originMethod) {
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
