package com.truward.polymer.domain.driver.spi;

import com.truward.di.InjectionContext;
import com.truward.polymer.core.driver.spi.SpecificationDriver;
import com.truward.polymer.domain.analysis.support.DefaultDomainAnalysisContext;
import com.truward.polymer.domain.driver.support.DefaultDomainObjectSpecifier;
import com.truward.polymer.domain.driver.support.DomainImplementerSettingsProvider;
import com.truward.polymer.domain.implementer.DomainObjectImplementer;

import javax.annotation.Nonnull;

/**
 * Domain specification driver that takes care of registering everything in the
 * global dependency injection context.
 *
 * @author Alexander Shabanov
 */
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
