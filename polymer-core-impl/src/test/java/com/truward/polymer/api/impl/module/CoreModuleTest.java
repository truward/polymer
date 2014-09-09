package com.truward.polymer.api.impl.module;

import com.google.common.collect.ImmutableList;
import com.truward.polymer.api.impl.test.plugin.bar.BarPlugin;
import com.truward.polymer.api.impl.test.plugin.foo.FooPlugin;
import com.truward.polymer.api.impl.test.specification.FooBarSpecification;
import com.truward.polymer.api.plugin.SpecificationPlugin;
import com.truward.polymer.api.processor.SpecificationHandler;
import org.junit.Test;

import javax.annotation.Nonnull;

import static org.junit.Assert.assertNotNull;

/**
 * Tests basic functionality of the {@link com.truward.polymer.api.impl.module.CoreModule}.
 *
 * @author Alexander Shabanov
 */
public final class CoreModuleTest {

  @Test
  public void shouldRegisterAndUseModules() {
    // Given:
    final TestCoreModule module = new TestCoreModule();
    module.addDefaults();

    // When:
    final SpecificationHandler handler = module.getInjectionContext().getBean(SpecificationHandler.class);
    final FooBarSpecification specification = handler.parseClass(FooBarSpecification.class);
    handler.done();

    // Then:
    assertNotNull(specification);
  }

  //
  // Test data
  //

  static final class TestCoreModule extends CoreModule {
    @Nonnull
    @Override
    protected Iterable<SpecificationPlugin> loadSpecificationPlugins() {
      return ImmutableList.<SpecificationPlugin>of(new BarPlugin(), new FooPlugin());
    }
  }
}
