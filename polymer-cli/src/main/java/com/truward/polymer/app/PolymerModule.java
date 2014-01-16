package com.truward.polymer.app;

import com.truward.di.InjectionContext;
import com.truward.di.support.DefaultInjectionContext;
import com.truward.polymer.core.driver.support.DefaultSpecificationHandler;
import com.truward.polymer.domain.analysis.support.DefaultDomainAnalysisContext;
import com.truward.polymer.domain.analysis.support.DefaultDomainImplementationTarget;
import com.truward.polymer.domain.analysis.support.DefaultDomainImplementationTargetProvider;
import com.truward.polymer.domain.driver.DefaultDomainObjectSpecifier;
import com.truward.polymer.domain.driver.DomainImplementerSettingsProvider;
import com.truward.polymer.domain.synthesis.DomainObjectImplementer;
import com.truward.polymer.domain.synthesis.support.DefaultDomainObjectImplementer;

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
    // domain framework-specific
    injectionContext.registerBean(DefaultDomainAnalysisContext.class);
    injectionContext.registerBean(DefaultDomainObjectSpecifier.class);
    injectionContext.registerBean(DomainImplementerSettingsProvider.class);
    injectionContext.registerBean(DefaultDomainObjectImplementer.class);
    injectionContext.registerBean(DefaultDomainImplementationTargetProvider.class);

    // general-purpose ones
    injectionContext.registerBean(DefaultSpecificationHandler.class);
    return this;
  }

  public InjectionContext getInjectionContext() {
    return injectionContext;
  }
}
