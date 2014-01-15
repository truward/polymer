package com.truward.polymer.domain.analysis.support;

import com.google.common.collect.ImmutableList;
import com.truward.polymer.domain.analysis.DomainAnalysisContext;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.DomainField;
import com.truward.polymer.domain.analysis.trait.GetterTrait;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author Alexander Shabanov
 */
public final class DefaultDomainAnalysisContext implements DomainAnalysisContext {

  private static final int FIELD_MAP_INITIAL_SIZE = 100;

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

    return new DefaultDomainAnalysisResult(clazz, parents, declaredFields, getAllFields(declaredFields, parents));
  }

  @Nonnull
  private static List<DomainField> getAllFields(@Nonnull List<DomainField> declaredFields,
                                                @Nonnull List<DomainAnalysisResult> parents) {
    final Map<String, DomainField> fieldMap = new LinkedHashMap<>(FIELD_MAP_INITIAL_SIZE);

    addDomainFields(fieldMap, declaredFields);
    for (final DomainAnalysisResult analysisResult : parents) {
      addDomainFields(fieldMap, analysisResult.getFields());
    }

    return ImmutableList.copyOf(fieldMap.values());
  }

  private static void addDomainFields(@Nonnull Map<String, DomainField> fieldMap, List<DomainField> fields) {
    for (final DomainField field : fields) {
      final String name = field.getFieldName();
      final DomainField prev = fieldMap.get(name);
      if (prev != null) {
        if (prev == field) {
          continue;
        }

        // different fields with the same name - error
        throw new RuntimeException("Different fields with the same name: " + field);
      }

      fieldMap.put(name, field);
    }
  }

  @Nonnull
  private static DomainField inferField(@Nonnull Method method) {
    final String methodName = method.getName();
    if (Names.isJavaBeanGetter(methodName)) {
      if (method.getParameterTypes().length > 0) {
        throw new RuntimeException("Unsupported getter " + method + "with parameters in the domain object");
      }

      final DomainField result = new DefaultDomainField(Names.asFieldName(method.getName()), method);
      result.putTrait(new GetterTrait(methodName));
      return result;
    }

    throw new RuntimeException("Unknown method signature: " + method);
  }
}
