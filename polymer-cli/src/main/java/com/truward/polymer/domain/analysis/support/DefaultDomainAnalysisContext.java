package com.truward.polymer.domain.analysis.support;

import com.truward.polymer.domain.analysis.DomainAnalysisContext;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.DomainField;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander Shabanov
 */
public final class DefaultDomainAnalysisContext implements DomainAnalysisContext {

  private final Map<Class<?>, DomainAnalysisResult> analysisResults = new HashMap<>();

  @Override
  @Nonnull
  public DomainAnalysisResult analyze(@Nonnull Class<?> clazz) {
    DomainAnalysisResult result = analysisResults.get(clazz);
    if (result != null) {
      if (result.isStub()) {
        throw new RuntimeException("Circular reference detected");
      }

      return result; // already analyzed
    }

    // put stub while analysis is in progress
    analysisResults.put(clazz, StubDomainAnalysisResult.getInstance());
    result = createDefaultAnalysisResult(clazz);
    analysisResults.put(clazz, result);
    return result;
  }

  //
  // Private
  //

  private DomainAnalysisResult createDefaultAnalysisResult(Class<?> clazz) {
    if (!clazz.isInterface()) {
      throw new IllegalArgumentException("Only interfaces are expected, got " + clazz);
    }

    // infer parents
    final Type[] interfaces = clazz.getGenericInterfaces();
    final List<DomainAnalysisResult> parents = new ArrayList<>(interfaces.length);
    for (final Type iface : interfaces) {
      if (!(iface instanceof Class)) {
        throw new RuntimeException("Parameterized types are not supported for domain model");
      }

      parents.add(analyze((Class) iface));
    }

    // infer fields
    final List<DomainField> declaredFields = new ArrayList<>();
    for (final Method method : clazz.getDeclaredMethods()) {
      declaredFields.add(inferField(method));
    }

    return new DefaultDomainAnalysisResult(clazz, parents, declaredFields);
  }

  private static DomainField inferField(Method method) {
    if (method.getParameterTypes().length > 0) {
      throw new RuntimeException("Unsupported method " + method + "with parameters in the domain object");
    }

    final String fieldName = NamingUtil.asFieldName(method.getName());

    final DomainField field = new MethodBasedDomainField(fieldName, method);

    // TODO: infer nullability
    if (field.isNullableUndecided()) {
      field.setNullable(false); // all fields are non-null by default
    }

    return field;
  }
}
