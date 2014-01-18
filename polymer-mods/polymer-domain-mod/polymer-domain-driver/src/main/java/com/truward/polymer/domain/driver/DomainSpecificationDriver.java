package com.truward.polymer.domain.driver;

import com.truward.di.InjectionContext;
import com.truward.polymer.core.driver.SpecificationDriver;
import com.truward.polymer.domain.analysis.support.DefaultDomainAnalysisContext;
import com.truward.polymer.domain.driver.support.DomainObjectImplementer;

import javax.annotation.Nonnull;

/**
 * Domain specification driver that takes care of registering everything in the global specification context.
 *
 * @author Alexander Shabanov
 */
@SuppressWarnings("UnusedDeclaration")
public final class DomainSpecificationDriver implements SpecificationDriver {
  @Override
  public void join(@Nonnull InjectionContext context) {
    // domain framework-specific
    context.registerBean(DefaultDomainAnalysisContext.class);
    context.registerBean(DefaultDomainObjectSpecifier.class);
    context.registerBean(DomainImplementerSettingsProvider.class);
    context.registerBean(DomainObjectImplementer.class);
  }
}
