package com.truward.polymer.api.impl.processor;

import com.truward.di.InjectionContext;
import com.truward.di.support.DefaultInjectionContext;
import com.truward.polymer.api.impl.test.plugin.bar.BarPlugin;
import com.truward.polymer.api.impl.test.plugin.foo.FooPlugin;
import com.truward.polymer.api.impl.test.specification.EmptySpecification;
import com.truward.polymer.api.impl.test.specification.FooBarSpecification;
import com.truward.polymer.api.impl.test.specification.FooSpecification;
import com.truward.polymer.api.impl.test.specification.OneEmptyMethodSpecification;
import com.truward.polymer.api.plugin.SpecificationState;
import com.truward.polymer.api.plugin.SpecificationStateAware;
import com.truward.polymer.api.processor.SpecificationHandler;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Alexander Shabanov
 */
public final class SpecificationHandlerTest {
  InjectionContext injectionContext;

  @Before
  public void init() {
    injectionContext = new DefaultInjectionContext();
    injectionContext.registerBean(DefaultSpecificationHandler.class);
  }

  @Test
  public void shouldDiscardEmptySpecification() {
    // Given:
    injectionContext.freeze();

    // When:
    final EmptySpecification specification = parseClass(EmptySpecification.class);

    // Then:
    assertNull(specification);
  }

  @Test
  public void shouldProcessDummySpecification() {
    // Given:
    injectionContext.freeze();

    // When:
    final OneEmptyMethodSpecification specification = parseClass(OneEmptyMethodSpecification.class);

    // Then:
    assertNotNull(specification);
  }

  @Test
  public void shouldProcessFooSpecification() {
    // Given:
    injectionContext.registerBean(FooPlugin.class);
    injectionContext.freeze();

    // When:
    final FooSpecification specification = parseClass(FooSpecification.class);

    // Then:
    assertNotNull(specification);
    assertEquals(3, specification.fooArg);
  }

  @Test
  public void shouldProcessMixedFooBarSpecificationInTheGivenOrder() {
    // Given:
    injectionContext.registerBean(FooPlugin.class);
    injectionContext.registerBean(BarPlugin.class);
    injectionContext.freeze();

    // When:
    final FooBarSpecification specification = parseClass(FooBarSpecification.class);

    // Then:
    assertNotNull(specification);
    assertEquals(1234567, specification.orderControl);
    assertEquals("123", specification.barArg);
    assertEquals(15, specification.fooArg);
  }

  @Test
  public void shouldChangeStateInAppropriateOrder() {
    // Given:
    final SpecificationStateAware plugin = mock(SpecificationStateAware.class);
    injectionContext.registerBean(plugin);
    injectionContext.freeze();

    // When:
    parseClass(OneEmptyMethodSpecification.class);

    // Then:
    verify(plugin).setState(SpecificationState.START);
    verify(plugin).setState(SpecificationState.RECORDING);
    verify(plugin).setState(SpecificationState.SUBMITTED);
    verify(plugin).setState(SpecificationState.COMPLETED);
  }

  //
  // Private
  //

  @Nullable
  private <T> T parseClass(@Nonnull Class<T> specificationClass) {
    final SpecificationHandler handler = injectionContext.getBean(SpecificationHandler.class);
    final T result = handler.parseClass(specificationClass);
    handler.done();
    return result;
  }
}
