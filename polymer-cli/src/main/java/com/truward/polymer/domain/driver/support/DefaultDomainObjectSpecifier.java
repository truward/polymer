package com.truward.polymer.domain.driver.support;

import com.truward.polymer.core.util.DefaultValues;
import com.truward.polymer.domain.DomainObjectSpecifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Alexander Shabanov
 */
public final class DefaultDomainObjectSpecifier implements DomainObjectSpecifier {
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
