package com.truward.polymer.core.support.driver;

import com.google.common.collect.ImmutableList;
import com.truward.di.InjectionContext;
import com.truward.di.InjectionException;
import com.truward.polymer.annotation.Specification;
import com.truward.polymer.core.driver.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Default implementation of {@link SpecificationHandler}
 *
 * @author Alexander Shabanov
 */
public final class DefaultSpecificationHandler implements SpecificationHandler {

  private final Logger log = LoggerFactory.getLogger(DefaultSpecificationHandler.class);

  @Resource
  private InjectionContext injectionContext;

  @Override
  @Nullable
  public <T> T parseClass(@Nonnull Class<T> clazz) {
    log.debug("Parsing {}", clazz);

    if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
      log.warn("Skipping {}: interface or abstract class can't be processed", clazz);
      return null;
    }

    final List<Method> specificationMethods = new ArrayList<>();
    for (final Method method : clazz.getMethods()) {
      if (method.getAnnotation(Specification.class) != null) {
        specificationMethods.add(method);
      }
    }

    if (specificationMethods.isEmpty()) {
      log.warn("No specification methods in {}", clazz);
      return null;
    }

    try {
      final T instance = clazz.newInstance();
      final List<Object> resources = provideResources(clazz, instance);
      final List<SpecificationStateAware> stateAwareBeans = new ArrayList<>();
      for (final Object resource : resources) {
        if (resource instanceof SpecificationStateAware) {
          stateAwareBeans.add((SpecificationStateAware) resource);
        }
      }

      // sort by ordinals
      Collections.sort(specificationMethods, SpecificationOrderComparator.INSTANCE);

      // invoke in the given order
      invokeSpecificationMethods(specificationMethods, instance, stateAwareBeans);

      log.debug("{} has been successfully processed", clazz);
      return instance;
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException("Uninstantiable " + clazz + ": no public default constructor", e);
    }
  }

  @Override
  public void done() {
    SpecificationUtil.notifyState(injectionContext.getBeans(SpecificationStateAware.class), SpecificationState.COMPLETED);
  }

  //
  // Private
  //

  /**
   * Compares two methods by looking to their ordinals.
   */
  private static final class SpecificationOrderComparator implements Comparator<Method> {
    static final SpecificationOrderComparator INSTANCE = new SpecificationOrderComparator();

    @Override
    public int compare(Method lhs, Method rhs) {
      return Integer.compare(getOrdinal(lhs), getOrdinal(rhs));
    }

    private static int getOrdinal(Method method) {
      final Specification specification = method.getAnnotation(Specification.class);
      return specification != null ? specification.ordinal() : Integer.MAX_VALUE;
    }
  }

  private void invokeSpecificationMethods(@Nonnull List<Method> specificationMethods,
                                          @Nonnull Object instance,
                                          @Nonnull List<SpecificationStateAware> stateAwareBeans) {
    for (final Method method : specificationMethods) {
      log.debug("Invoking specification method {}", method);

      SpecificationUtil.notifyState(stateAwareBeans, SpecificationState.RECORDING);
      try {
        invokeMethod(instance, method);
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new RuntimeException("Error while invoking " + method, e);
      }
      SpecificationUtil.notifyState(stateAwareBeans, SpecificationState.SUBMITTED);
    }
  }

  private void invokeMethod(Object instance, Method method) throws InvocationTargetException, IllegalAccessException {
    final Specification specification = method.getAnnotation(Specification.class);
    if (specification == null) {
      return;
    }

    if (!Modifier.isPublic(method.getModifiers())) {
      throw new RuntimeException("Non-public specification method: " + method);
    }

    // fetch parameters
    final Object[] parameters;
    final Class<?>[] parameterTypes = method.getParameterTypes();
    final int parameterCount = parameterTypes.length;
    if (parameterCount > 0) {
      final Annotation[][] paramListAnnotations = method.getParameterAnnotations();
      parameters = new Object[parameterCount];
      final List<SpecificationParameterProvider> parameterProviders = injectionContext.getBeans(SpecificationParameterProvider.class);
      if (parameterProviders.size() == 0) {
        throw new RuntimeException("No specification parameter provides; method parameters will be left unprovided");
      }

      // provide parameters
      for (int i = 0; i < parameterCount; ++i) {
        boolean provided = false;
        final Class<?> parameterType = parameterTypes[i];
        final List<Annotation> paramAnnotations = ImmutableList.copyOf(paramListAnnotations[i]);
        for (final SpecificationParameterProvider parameterProvider : parameterProviders) {
          if (parameterProvider.canProvideParameter(paramAnnotations, parameterType)) {
            if (!provided) {
              parameters[i] = parameterProvider.provideParameter(paramAnnotations, parameterType);
              provided = true;
            } else {
              log.error("Multiple specifiers can provide the same parameter annotated with {} for method {}",
                  paramAnnotations, method);
            }
          }
        }

        if (!provided) {
          throw new RuntimeException("Unable to provide parameter for specification method");
        }
      }
    } else {
      parameters = new Object[0];
    }

    // unexpected result
    if (!void.class.equals(method.getReturnType())) {
      log.warn("Result of the specification method {} will be ignored", method);
    }

    method.invoke(instance, parameters);
  }

  @Nonnull
  private List<Object> provideResources(@Nonnull Class<?> clazz, @Nonnull Object instance) {
    final List<Object> providedResources = new ArrayList<>();
    try {
      for (final Field field : clazz.getDeclaredFields()) {
        final Object providedResource = provideFieldValue(clazz, instance, field);
        if (providedResource != null) {
          providedResources.add(providedResource);
        }
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Unable to set field value", e);
    }
    return ImmutableList.copyOf(providedResources);
  }

  @Nullable
  private Object provideFieldValue(Class<?> clazz, Object instance, Field field) throws IllegalAccessException {
    final Resource resource = field.getAnnotation(Resource.class);
    if (resource == null) {
      return null;
    }

    if (resource.mappedName().length() > 0) {
      log.warn("Resource name ignored: {} for {} in {}", resource.mappedName(), clazz, field);
    }

    boolean wasAccessible = field.isAccessible();
    field.setAccessible(true);

    assert field.get(instance) == null : "Unexpected assigned value";

    final Object injectedBean;
    try {
      injectedBean = injectionContext.getBean(field.getType());
    } catch (InjectionException e) {
      throw new RuntimeException(String.format("Can't inject bean into %s in %s", field, clazz), e);
    }
    field.set(instance, injectedBean);

    if (!wasAccessible) {
      field.setAccessible(false);
    }

    return injectedBean;
  }
}
