package com.truward.polymer.marshal;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.truward.di.InjectionContext;
import com.truward.polymer.core.driver.SpecificationDriver;
import com.truward.polymer.core.output.OutputStreamProvider;
import com.truward.polymer.core.support.PolymerModule;
import com.truward.polymer.domain.driver.support.DomainSpecificationDriver;
import com.truward.polymer.marshal.gson.support.GsonMarshallingDriver;
import com.truward.polymer.marshal.jackson.support.JacksonMarshallingDriver;
import com.truward.polymer.marshal.json.GsonMarshallingSpecifier;
import com.truward.polymer.marshal.json.JacksonMarshallingSpecifier;
import com.truward.polymer.marshal.rest.support.RestExposureDriver;
import org.junit.Test;

import java.util.List;
import java.util.ServiceLoader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

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
        ImmutableSet.of(DomainSpecificationDriver.class, GsonMarshallingDriver.class, JacksonMarshallingDriver.class, RestExposureDriver.class),
        ImmutableSet.copyOf(Lists.transform(drivers, new Function<SpecificationDriver, Class<?>>() {
          @Override
          public Class<?> apply(SpecificationDriver input) {
            return input.getClass();
          }
        })));
  }

  @Test
  public void shouldJoinContext() {
    final PolymerModule module = new PolymerModule();
    module.addDefaults();
    // OK, module enabled defaults

    // Ensure specifiers can be fetched
    final InjectionContext context = module.getInjectionContext();
    context.registerBean(mock(OutputStreamProvider.class));

    // Freeze context
    context.freeze();

    assertNotNull(context.getBean(JacksonMarshallingSpecifier.class));
    assertNotNull(context.getBean(GsonMarshallingSpecifier.class));
  }
}
