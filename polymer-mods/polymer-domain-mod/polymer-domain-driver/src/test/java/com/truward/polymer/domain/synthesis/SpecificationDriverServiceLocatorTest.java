package com.truward.polymer.domain.synthesis;

import com.google.common.collect.ImmutableList;
import com.truward.polymer.core.driver.spi.SpecificationDriver;
import com.truward.polymer.domain.driver.spi.DomainSpecificationDriver;
import org.junit.Test;

import java.util.List;
import java.util.ServiceLoader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests, that service locator is not broken by refactoring
 *
 * @author Alexander Shabanov
 */
public final class SpecificationDriverServiceLocatorTest {

  @Test
  public void shouldLocateDomainSpecificationDriver() {
    final ServiceLoader<SpecificationDriver> driverServiceLoader = ServiceLoader.load(SpecificationDriver.class);
    final List<SpecificationDriver> drivers = ImmutableList.copyOf(driverServiceLoader);
    assertEquals("Should find one driver", 1, drivers.size());
    assertTrue("Driver should be an instance of DomainSpecificationDriver",
        drivers.get(0) instanceof DomainSpecificationDriver);
  }
}
