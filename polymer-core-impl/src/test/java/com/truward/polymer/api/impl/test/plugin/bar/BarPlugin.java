package com.truward.polymer.api.impl.test.plugin.bar;

import com.truward.di.InjectionContext;
import com.truward.polymer.api.plugin.SpecificationParameterProvider;
import com.truward.polymer.api.plugin.SpecificationPlugin;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author Alexander Shabanov
 */
public final class BarPlugin implements SpecificationParameterProvider, SpecificationPlugin {
  int counter;

  @Override
  public <R> boolean canProvideParameter(@Nonnull List<? extends Annotation> annotations, @Nonnull Class<R> resultType) {
    return resultType == String.class && annotations.size() == 1 &&
        annotations.get(0).annotationType().equals(BarArgument.class);
  }

  @Nonnull
  @Override
  public <R> R provideParameter(@Nonnull List<? extends Annotation> annotations, @Nonnull Class<R> resultType) {
    assertTrue(canProvideParameter(annotations, resultType));
    return resultType.cast(String.valueOf(++counter));
  }

  @Override
  public void join(@Nonnull InjectionContext context) {
    context.registerBean(this);
  }
}
