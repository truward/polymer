package com.truward.polymer.domain.analysis;

import com.google.common.collect.ImmutableList;

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

  public DomainAnalysisResult(Class<?> clazz) {
    originClass = clazz;

    final List<DomainField> fields = new ArrayList<>();

    //final Type[] gts = clazz.getGenericInterfaces();
    //final Method[] declaredMethods = clazz.getDeclaredMethods();
    for (final Method method : clazz.getDeclaredMethods()) {
      fields.add(inferField(method));
    }

    this.declaredFields = ImmutableList.copyOf(fields);
  }

  public Class<?> getOriginClass() {
    return originClass;
  }

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

    private MethodBasedDomainField(String name, Method originMethod) {
      if (name == null) {
        throw new IllegalArgumentException("name");
      }
      if (originMethod == null) {
        throw new IllegalArgumentException("originMethod");
      }

      this.name = name;
      this.originMethod = originMethod;
      this.getterName = createGetterName(getFieldType(), getFieldName());

      // all primitive types are not nullable
      if (getFieldType() instanceof Class && ((Class) getFieldType()).isPrimitive()) {
        setNullable(false);
      }
    }

    @Override
    public String getFieldName() {
      return name;
    }

    @Override
    public String getGetterName() {
      return getterName;
    }

    @Override
    public Type getFieldType() {
      return originMethod.getGenericReturnType();
    }

    @Override
    public boolean isNullable() {
      if (this.nullable == null) {
        throw new IllegalStateException("Nullable value twice");
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

    final String fieldName = asFieldName(method.getName());

    final DomainField field = new MethodBasedDomainField(fieldName, method);

    // TODO: infer nullability
    if (field.isNullableUndecided()) {
      field.setNullable(false);
    }

    return field;
  }

  private static String asFieldName(String methodName) {
    final String getPrefix = "get";
    final int getPrefixLength = getPrefix.length();

    if (!methodName.startsWith(getPrefix)) {
      throw new RuntimeException("Unsupported non-get method " + methodName + "in the domain object");
    }
    final char[] fieldNameChars = new char[methodName.length() - getPrefixLength];
    methodName.getChars(getPrefixLength, methodName.length(), fieldNameChars, 0);
    fieldNameChars[0] = Character.toLowerCase(fieldNameChars[0]);
    return new String(fieldNameChars);
  }

  private static String createGetterName(Type fieldType, String fieldName) {
    final String prefix = fieldType.equals(Boolean.TYPE) ? "is" : "get";
    final StringBuilder nameBuilder = new StringBuilder(prefix.length() + fieldName.length());
    nameBuilder.append(prefix);
    nameBuilder.append(Character.toUpperCase(fieldName.charAt(0)));
    nameBuilder.append(fieldName.subSequence(1, fieldName.length()));
    return nameBuilder.toString();
  }
}
