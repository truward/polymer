package com.truward.polymer.app;

import com.truward.di.InjectionContext;
import com.truward.di.support.DefaultInjectionContext;
import com.truward.polymer.core.driver.SpecificationDriver;
import com.truward.polymer.core.driver.SpecificationStateAware;
import com.truward.polymer.core.driver.support.DefaultSpecificationHandler;
import com.truward.polymer.domain.driver.support.DomainSpecificationDriver;

/**
 * Represents dependency injection module that registers default configuration for the polymer application.
 *
 * @author Alexander Shabanov
 */
public class PolymerModule {
  private final InjectionContext injectionContext;

  public PolymerModule() {
    injectionContext = new DefaultInjectionContext();
  }

  public PolymerModule addDefaults() {
    injectionContext.registerBean(DomainSpecificationDriver.class);
    injectionContext.registerBean(new DefaultSpecificationHandler(injectionContext.getBeans(SpecificationDriver.class),
        injectionContext.getBeans(SpecificationStateAware.class)));
    return this;
  }

  public InjectionContext getInjectionContext() {
    return injectionContext;
  }
}
