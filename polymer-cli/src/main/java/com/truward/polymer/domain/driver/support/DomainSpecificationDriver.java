package com.truward.polymer.domain.driver.support;

import com.google.common.collect.ImmutableList;
import com.truward.polymer.core.driver.SpecificationDriver;
import com.truward.polymer.core.driver.SpecificationState;
import com.truward.polymer.core.driver.SpecificationStateAware;
import com.truward.polymer.domain.DomainObjectSpecifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Implementation of the specification driver for domain object generator plugin
 *
 * @author Alexander Shabanov
 */
public final class DomainSpecificationDriver implements SpecificationDriver, SpecificationStateAware {
  private final Logger log = LoggerFactory.getLogger(DomainSpecificationDriver.class);
  private final DomainObjectSpecifier specifier;

  public DomainSpecificationDriver() {
    specifier = new DefaultDomainObjectSpecifier();
  }

  @Nonnull
  @Override
  public List<Class<?>> getProvidedResourceClasses() {
    return ImmutableList.<Class<?>>of(DomainObjectSpecifier.class);
  }

  @Nonnull
  @Override
  public Object provide(@Nonnull Class<?> clazz) {
    if (clazz.equals(DomainObjectSpecifier.class)) {
      return specifier;
    }
    throw new IllegalStateException("Unable to provide resource for class " + clazz);
  }

  @Override
  public void setState(@Nonnull SpecificationState state) {
    log.debug("Changed state to {}", state);
  }
}
