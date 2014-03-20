package com.truward.polymer.marshal;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.truward.polymer.core.driver.SpecificationDriver;
import com.truward.polymer.domain.driver.support.DomainSpecificationDriver;
import com.truward.polymer.marshal.gson.support.GsonMarshallingDriver;
import com.truward.polymer.marshal.jackson.support.JacksonMarshallingDriver;
import com.truward.polymer.marshal.rest.support.RestExposureDriver;
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
    assertEquals(
        ImmutableSet.of(DomainSpecificationDriver.class, GsonMarshallingDriver.class, JacksonMarshallingDriver.class,
            RestExposureDriver.class),
        ImmutableSet.copyOf(Lists.transform(drivers, new Function<SpecificationDriver, Class<?>>() {
          @Override
          public Class<?> apply(SpecificationDriver input) {
            return input.getClass();
          }
        })));
  }
}
