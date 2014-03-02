package com.truward.polymer.domain.driver.support;

import com.google.common.base.Predicate;
import com.truward.polymer.core.driver.SpecificationParameterProvider;
import com.truward.polymer.core.driver.SpecificationState;
import com.truward.polymer.core.driver.SpecificationStateAware;
import com.truward.polymer.core.types.DefaultValues;
import com.truward.polymer.domain.*;
import com.truward.polymer.domain.analysis.*;
import com.truward.polymer.domain.analysis.support.GenDomainClass;
import com.truward.polymer.naming.FqName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public final class DefaultDomainObjectSpecifier implements DomainObjectSpecifier, SpecificationStateAware,
    SpecificationParameterProvider {
  private final Logger log = LoggerFactory.getLogger(DefaultDomainObjectSpecifier.class);

  private SpecificationState state = SpecificationState.HOLD;

  @Resource
  private DomainAnalysisContext analysisContext;

  @Resource
  private DomainImplementerSettings implementerSettings;

  @Resource
  private DomainImplementationTargetSink targetSink;

  private DomainAnalysisResult currentAnalysisResult;
  private DomainField currentField;


  @Override
  public DomainObjectSpecifier target(@Nonnull Class<?>... classes) {
    for (final Class<?> targetClass : classes) {
      targetSink.submit(analysisContext.analyze(targetClass));
    }

    return this;
  }

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
    return putFieldTrait(FieldTrait.NULLABLE);
  }

  @Nonnull
  @Override
  public DomainObjectSpecifier isNonNull(Object field) {
    return putFieldTrait(FieldTrait.NONNULL);
  }

  @Override
  @Nonnull
  public DomainObjectSpecifier hasLength(String field) {
    return putFieldTrait(FieldTrait.HAS_LENGTH);
  }

  @Override
  @Nonnull
  public DomainObjectSpecifier isNonNegative(int field) {
    return putFieldTrait(FieldTrait.NON_NEGATIVE);
  }

  @Override
  public void setState(@Nonnull SpecificationState state) {
    this.state = state;
  }

  @Nonnull
  @Override
  public DomainObjectSettings getObjectSettings(@Nonnull Class<?> clazz) {
    return new DefaultDomainObjectSettings(analysisContext.analyze(clazz));
  }

  @Nonnull
  @Override
  public DomainImplementerSettings getImplementerSettings() {
    return implementerSettings;
  }

  //
  // Private
  //

  private DomainObjectSpecifier putFieldTrait(@Nonnull FieldTrait fieldTrait) {
    checkRecordingStateAndField();
    try {
      currentField.putTrait(fieldTrait);
    } finally {
      currentField = null;
    }

    return this;
  }

  private void checkRecordingStateAndField() {
    checkRecordingState();
    if (currentField == null) {
      throw new UnsupportedOperationException("Unknown current field");
    }
  }

  private void checkRecordingState() {
    if (currentAnalysisResult == null) {
      throw new IllegalStateException("No current object");
    }

    if (state != SpecificationState.RECORDING) {
      throw new IllegalStateException("Illegal specification state");
    }
  }

  private final class DefaultDomainObjectSettings implements DomainObjectSettings {

    private final DomainAnalysisResult analysisResult;

    private DefaultDomainObjectSettings(@Nonnull DomainAnalysisResult analysisResult) {
      this.analysisResult = analysisResult;
    }

    @Override
    public DomainObjectBuilderSettings assignBuilder() {
      final GenDomainClass.GenBuilderClass settings = getDomainClass().getGenBuilderClass();
      settings.setSupported(true);
      return settings;
    }

    @Override
    public void setTargetName(@Nonnull FqName implementationName) {
      getDomainClass().setFqName(implementationName);
    }

    @Nonnull
    private GenDomainClass getDomainClass() {
      final GenDomainClass genDomainClass = targetSink.getTarget(analysisResult);
      if (genDomainClass == null) {
        throw new IllegalStateException("Can't assign builder: it is not known whether the class has " +
            "the corresponding generation target or not");
      }
      return genDomainClass;
    }
  }

  private final class StateAwareInvocationHandler implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      checkRecordingState();
      log.debug("Invocation of {}", method);
      final String methodName = method.getName();
      currentField = getField(currentAnalysisResult, new Predicate<DomainField>() {
        @Override
        public boolean apply(DomainField input) {
          final String getterName = FieldUtil.getMethodName(input, OriginMethodRole.GETTER);
          // TODO: check types, getter form, etc.
          return methodName.equals(getterName);
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
