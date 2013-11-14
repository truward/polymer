package com.truward.polymer.domain.driver.support;

import com.google.common.collect.ImmutableList;
import com.truward.polymer.core.driver.SpecificationDriver;
import com.truward.polymer.core.driver.SpecificationState;
import com.truward.polymer.core.util.DefaultValues;
import com.truward.polymer.domain.DomainObjectSpecifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * TODO: split into two classes: DefaultDomainObjectSpecifier & DomainSpecificationDriver
 *
 * @author Alexander Shabanov
 */
public final class DefaultDomainObjectSpecifier implements DomainObjectSpecifier, SpecificationDriver {
  private final Logger log = LoggerFactory.getLogger(DefaultDomainObjectSpecifier.class);

  @Override
  public <T> T domainObject(Class<T> clazz) {
    if (!clazz.isInterface()) {
      throw new RuntimeException("Domain class expected to be an interface");
    }

    final T instance = clazz.cast(Proxy.newProxyInstance(
        Thread.currentThread().getContextClassLoader(),
        new Class[] { clazz },
        new StateAwareInvocationHandler()));
    log.debug("Returned proxy instance for class {}", clazz);

    return instance;
  }

  @Override
  public DomainObjectSpecifier isNullable(Object field) {
    return this;
  }

  @Override
  public DomainObjectSpecifier hasLength(String field) {
    return this;
  }

  @Override
  public DomainObjectSpecifier isNonNegative(int field) {
    return this;
  }

  @Nonnull
  @Override
  public List<Class<?>> getProvidedResourceClasses() {
    return ImmutableList.<Class<?>>of(DomainObjectSpecifier.class);
  }

  @Nonnull
  @Override
  public Object provide(@Nonnull Class<?> clazz) {
    if (clazz.equals(DomainObjectSpecifier.class)) {
      return this;
    }
    throw new IllegalStateException("Unable to provide resource for class " + clazz);
  }

  @Override
  public void setState(@Nonnull SpecificationState state) {
    log.debug("Changed state to {}", state);
  }

  //
  // Private
  //

  private final class StateAwareInvocationHandler implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      log.debug("Invocation of {}", method);
      return DefaultValues.getDefaultValueFor(method.getReturnType());
    }
  }
}
