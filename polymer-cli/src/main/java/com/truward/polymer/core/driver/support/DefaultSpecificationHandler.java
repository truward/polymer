package com.truward.polymer.core.driver.support;

import com.google.common.collect.ImmutableMap;
import com.truward.polymer.annotation.Specification;
import com.truward.polymer.core.driver.SpecificationDriver;
import com.truward.polymer.core.driver.SpecificationHandler;
import com.truward.polymer.core.driver.SpecificationState;
import com.truward.polymer.core.driver.SpecificationStateAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of {@link SpecificationHandler}
 *
 * @author Alexander Shabanov
 */
public final class DefaultSpecificationHandler implements SpecificationHandler {

  private final Logger log = LoggerFactory.getLogger(DefaultSpecificationHandler.class);

  private final Map<Class<?>, SpecificationDriver> driverMap;
  private final List<SpecificationStateAware> stateAwareBeans;

  public DefaultSpecificationHandler(List<SpecificationDriver> drivers, List<SpecificationStateAware> stateAwareBeans) {
    this.driverMap = toMap(drivers);
    this.stateAwareBeans = stateAwareBeans;
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

    if (method.getParameterTypes().length != 0) {
      throw new RuntimeException("Only parameterless specification methods are supported, got: " + method);
    }

    if (!void.class.equals(method.getReturnType())) {
      log.warn("Result of the specification method " + method + " will be ignored");
    }

    method.invoke(instance);
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

    final SpecificationDriver driver = driverMap.get(field.getType());
    if (driver == null) {
      throw new RuntimeException("Unable to provide resource for field " + field);
    }

    boolean wasAccessible = field.isAccessible();
    field.setAccessible(true);

    assert field.get(instance) == null : "Unexpected assigned value";
    field.set(instance, driver.provide(field.getType()));

    if (!wasAccessible) {
      field.setAccessible(wasAccessible);
    }
  }

  private static Map<Class<?>, SpecificationDriver> toMap(List<SpecificationDriver> drivers) {
    final Map<Class<?>, SpecificationDriver> result = new HashMap<>();
    for (final SpecificationDriver driver : drivers) {
      for (final Class<?> clazz : driver.getProvidedResourceClasses()) {
        if (result.containsKey(clazz)) {
          throw new RuntimeException("Driver " + driver + " and " + result.get(clazz) + " provide same class " + clazz);
        }

        result.put(clazz, driver);
      }
    }

    return ImmutableMap.copyOf(result);
  }
}
