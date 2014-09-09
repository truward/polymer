package com.truward.polymer.api.impl.module;

import com.truward.di.InjectionContext;
import com.truward.di.support.DefaultInjectionContext;
import com.truward.polymer.api.impl.processor.DefaultSpecificationHandler;
import com.truward.polymer.api.plugin.SpecificationPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ServiceLoader;

/**
 * @author Alexander Shabanov
 */
public class CoreModule {
  private final Logger log = LoggerFactory.getLogger(getClass());

  private final InjectionContext injectionContext;

  public CoreModule() {
    injectionContext = new DefaultInjectionContext();
  }

  @Nonnull
  public final CoreModule addDefaults() {
    registerCoreBeans();

    // service-loader related
    final Iterable<SpecificationPlugin> pluginServiceLoader = loadSpecificationPlugins();
    for (final SpecificationPlugin specificationPlugin : pluginServiceLoader) {
      log.info("Using driver: {}", specificationPlugin);
      specificationPlugin.join(injectionContext);
    }

    return this;
  }

  @Nonnull
  public final InjectionContext getInjectionContext() {
    return injectionContext;
  }

  //
  // Protected
  //

  @Nonnull
  protected Iterable<SpecificationPlugin> loadSpecificationPlugins() {
    return ServiceLoader.load(SpecificationPlugin.class);
  }

  /**
   * Register general-purpose beans
   */
  protected void registerCoreBeans() {
    injectionContext.registerBean(DefaultSpecificationHandler.class);
  }
}
