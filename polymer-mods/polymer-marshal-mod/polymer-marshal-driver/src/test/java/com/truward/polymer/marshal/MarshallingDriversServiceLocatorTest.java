package com.truward.polymer.marshal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.truward.polymer.core.driver.SpecificationDriver;
import com.truward.polymer.domain.driver.support.DomainSpecificationDriver;
import com.truward.polymer.marshal.gson.support.driver.GsonMarshallingDriver;
import com.truward.polymer.marshal.rest.support.driver.RestExposureDriver;
import org.junit.Test;

import java.util.List;
import java.util.ServiceLoader;

import static org.junit.Assert.assertEquals;

/**
 * Tests, that gson marshalling driver can be found along with domain object driver
 *
 * @author Alexander Shabanov
 */
public final class MarshallingDriversServiceLocatorTest {

  @Test
  public void shouldLocateDrivers() {
    final ServiceLoader<SpecificationDriver> driverServiceLoader = ServiceLoader.load(SpecificationDriver.class);
    final List<SpecificationDriver> drivers = ImmutableList.copyOf(driverServiceLoader);
    assertEquals("Should find three drivers", 3, drivers.size());
    assertEquals(ImmutableSet.of(DomainSpecificationDriver.class, GsonMarshallingDriver.class, RestExposureDriver.class),
        ImmutableSet.<Class<?>>of(drivers.get(0).getClass(), drivers.get(1).getClass(), drivers.get(2).getClass()));
  }
}
