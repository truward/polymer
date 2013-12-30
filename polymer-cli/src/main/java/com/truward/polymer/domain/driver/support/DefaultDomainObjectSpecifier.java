package com.truward.polymer.domain.driver.support;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.truward.polymer.core.driver.SpecificationParameterProvider;
import com.truward.polymer.core.driver.SpecificationState;
import com.truward.polymer.core.driver.SpecificationStateAware;
import com.truward.polymer.code.naming.FqName;
import com.truward.polymer.core.util.DefaultValues;
import com.truward.polymer.domain.DomainObject;
import com.truward.polymer.domain.DomainObjectSpecifier;
import com.truward.polymer.domain.analysis.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public final class DefaultDomainObjectSpecifier implements DomainObjectSpecifier, SpecificationStateAware,
    SpecificationParameterProvider, DomainImplTargetProvider {
  private final Logger log = LoggerFactory.getLogger(DefaultDomainObjectSpecifier.class);

  private SpecificationState state = SpecificationState.HOLD;

  @Resource
  private DomainAnalysisContext analysisContext;

  private DomainAnalysisResult currentAnalysisResult;
  private DomainField currentField;
  private List<DomainImplTarget> implementationTargets = new ArrayList<>();

  @Override
  @Nonnull
  public <T> T domainObject(@Nonnull Class<T> clazz) {
    if (!clazz.isInterface()) {
      throw new RuntimeException("Domain class expected to be an interface");
    }

    final T instance = clazz.cast(Proxy.newProxyInstance(
        Thread.currentThread().getContextClassLoader(),
        new Class[] { clazz },
        new StateAwareInvocationHandler()));
    log.debug("Returned proxy instance for class {}", clazz);

    currentAnalysisResult = analysisContext.analyze(clazz);

    // automatically introduce implementation target
    implementationTargets.add(new DomainImplTarget(currentAnalysisResult,
        FqName.parse("com.mysite." + currentAnalysisResult.getOriginClass().getName() + "Impl")));

    return instance;
  }

  @Override
  public <T extends Annotation, R> boolean canProvideParameter(@Nonnull List<T> annotations,
                                                               @Nonnull Class<R> resultType) {
    return annotations.size() == 1 && DomainObject.class.isAssignableFrom(annotations.get(0).getClass());
  }

  @Override
  public <T extends Annotation, R> R provideParameter(@Nonnull List<T> annotations, @Nonnull Class<R> resultType) {
    if (!canProvideParameter(annotations, resultType)) {
      throw new IllegalStateException("Unexpected param annotation type"); // should not come here if used properly
    }

    return domainObject(resultType);
  }

  @Override
  @Nonnull
  public DomainObjectSpecifier isNullable(Object field) {
    checkSpecStateAndField();
    try {
      if (!currentField.isNullableUndecided()) {
        if (currentField.isNullable()) {
          log.warn("Duplicate nullability specifier");
          return this;
        }
        throw new UnsupportedOperationException("Nullability has already been specified");
      }

      currentField.setNullable(true);
    } finally {
      currentField = null;
    }

    return this;
  }

  @Override
  @Nonnull
  public DomainObjectSpecifier hasLength(String field) {
    checkSpecStateAndField();
    // TODO: hasLength, check return type
    // TODO: tear-off constraint to field?
    return this;
  }

  @Override
  @Nonnull
  public DomainObjectSpecifier isNonNegative(int field) {
    checkSpecStateAndField();
    // TODO: isNonNegative
    return this;
  }

  @Override
  public void setState(@Nonnull SpecificationState state) {
    this.state = state;
  }

  @Nonnull
  @Override
  public List<DomainImplTarget> getImplementationTargets() {
    return ImmutableList.copyOf(implementationTargets);
  }

  //
  // Private
  //

  private void checkSpecStateAndField() {
    checkSpecState();
    if (currentField == null) {
      throw new UnsupportedOperationException("Unknown current field");
    }
  }

  private void checkSpecState() {
    if (currentAnalysisResult == null) {
      throw new IllegalStateException("No current object");
    }

    if (state != SpecificationState.RECORDING) {
      throw new IllegalStateException("Illegal specification state");
    }
  }

  private final class StateAwareInvocationHandler implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      checkSpecState();
      log.debug("Invocation of {}", method);
      final String methodName = method.getName();
      currentField = getField(currentAnalysisResult, new Predicate<DomainField>() {
        @Override
        public boolean apply(DomainField input) {
          return methodName.equals(input.getGetterName());
        }
      });

      if (currentField == null) {
        throw new IllegalStateException("No field corresponds to method " + methodName);
      }

      return DefaultValues.getDefaultValueFor(method.getReturnType());
    }
  }

  // TODO: reusable
  private static DomainField getField(DomainAnalysisResult analysisResult, Predicate<DomainField> predicate) {
    for (final DomainField field : analysisResult.getDeclaredFields()) {
      if (predicate.apply(field)) {
        return field;
      }
    }

    for (final DomainAnalysisResult parent : analysisResult.getParents()) {
      final DomainField field = getField(parent, predicate);
      if (field != null) {
        return field;
      }
    }

    return null;
  }
}
