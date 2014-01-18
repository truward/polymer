package com.truward.polymer.core;

import com.truward.di.InjectionContext;
import com.truward.di.support.DefaultInjectionContext;
import com.truward.polymer.core.driver.SpecificationDriver;
import com.truward.polymer.core.driver.support.DefaultSpecificationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

/**
 * Represents dependency injection module that registers default configuration for the polymer application.
 *
 * @author Alexander Shabanov
 */
public class PolymerModule {
  private final Logger log = LoggerFactory.getLogger(PolymerModule.class);

  private final InjectionContext injectionContext;

  public PolymerModule() {
    injectionContext = new DefaultInjectionContext();
  }

  public PolymerModule addDefaults() {
    // general-purpose ones
    injectionContext.registerBean(DefaultSpecificationHandler.class);

    // service-loader related
    final ServiceLoader<SpecificationDriver> driverServiceLoader = ServiceLoader.load(SpecificationDriver.class);
    for (final SpecificationDriver driver : driverServiceLoader) {
      log.info("Using driver: {}", driver);
      driver.join(injectionContext);
    }

    return this;
  }

  public InjectionContext getInjectionContext() {
    return injectionContext;
  }
}
