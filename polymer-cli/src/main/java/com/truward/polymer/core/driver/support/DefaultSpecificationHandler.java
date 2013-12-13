package com.truward.polymer.core.driver.support;

import com.google.common.collect.ImmutableList;
import com.truward.di.InjectionContext;
import com.truward.polymer.annotation.Specification;
import com.truward.polymer.core.driver.SpecificationHandler;
import com.truward.polymer.core.driver.SpecificationParameterProvider;
import com.truward.polymer.core.driver.SpecificationState;
import com.truward.polymer.core.driver.SpecificationStateAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of {@link SpecificationHandler}
 *
 * @author Alexander Shabanov
 */
public final class DefaultSpecificationHandler implements SpecificationHandler {

  private final Logger log = LoggerFactory.getLogger(DefaultSpecificationHandler.class);

  private List<SpecificationStateAware> stateAwareBeans;

  @Resource
  private InjectionContext injectionContext;

  @PostConstruct
  public void init() {
    stateAwareBeans = injectionContext.getBeans(SpecificationStateAware.class);
  }

  @Override
  public boolean parseClass(Class<?> clazz) {
    if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
      log.debug("Skipping interface or abstract class: {}", clazz);
      return false;
    }

    final List<Method> specificationMethods = new ArrayList<>();
    for (final Method method : clazz.getMethods()) {
      if (method.getAnnotation(Specification.class) != null) {
        specificationMethods.add(method);
      }
    }

    if (specificationMethods.isEmpty()) {
      return false;
    }

    try {
      final Object instance = clazz.newInstance();
      provideResources(clazz, instance);
      invokeSpecificationMethods(specificationMethods, instance);
      return true;
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException("Uninstantiable class: no public default constructor", e);
    }
  }

  //
  // Private
  //

  private void invokeSpecificationMethods(List<Method> specificationMethods, Object instance) {
    try {
      for (final Method method : specificationMethods) {
        // all the drivers put to recording state
        // TODO: only the specific drivers should know about it
        for (final SpecificationStateAware bean : stateAwareBeans) {
          bean.setState(SpecificationState.RECORDING);
        }

        invokeMethod(instance, method);

        // all the drivers put to submitted state
        for (final SpecificationStateAware bean : stateAwareBeans) {
          bean.setState(SpecificationState.SUBMITTED);
        }
      }
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException("Unable to invoke method", e);
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
              log.warn("Multiple specifiers can provide same parameter"); // TODO: error?
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
      log.warn("Result of the specification method " + method + " will be ignored");
    }

    method.invoke(instance, parameters);
  }

  private void provideResources(Class<?> clazz, Object instance) {
    try {
      for (final Field field : clazz.getDeclaredFields()) {
        provideFieldValue(clazz, instance, field);
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Unable to set field value", e);
    }
  }

  private void provideFieldValue(Class<?> clazz, Object instance, Field field) throws IllegalAccessException {
    final Resource resource = field.getAnnotation(Resource.class);
    if (resource == null) {
      return;
    }

    if (resource.mappedName().length() > 0) {
      log.warn("Resource name ignored: '" + resource.mappedName() + "' for " + clazz);
    }

    boolean wasAccessible = field.isAccessible();
    field.setAccessible(true);

    assert field.get(instance) == null : "Unexpected assigned value";

    // TODO: handle injection exception
    field.set(instance, injectionContext.getBean(field.getType()));

    if (!wasAccessible) {
      field.setAccessible(wasAccessible);
    }
  }
}
