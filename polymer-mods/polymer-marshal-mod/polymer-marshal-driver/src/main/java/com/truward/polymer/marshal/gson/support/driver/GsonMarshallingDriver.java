package com.truward.polymer.marshal.gson.support.driver;

import com.truward.di.InjectionContext;
import com.truward.polymer.core.driver.SpecificationDriver;
import com.truward.polymer.marshal.gson.support.analysis.DefaultGsonMarshallerImplementer;
import com.truward.polymer.marshal.gson.support.specification.DefaultGsonMarshallingSpecifier;

import javax.annotation.Nonnull;

/**
 * GSON marshalling driver that takes care of registering everything related to GSON marshalling specifications
 * in the global dependency injection context.
 *
 * @author Alexander Shabanov
 */
@SuppressWarnings("UnusedDeclaration") // false positive, this driver is used in ServiceLoader config
public final class GsonMarshallingDriver implements SpecificationDriver {
  @Override
  public void join(@Nonnull InjectionContext context) {
    context.registerBean(DefaultGsonMarshallingSpecifier.class);
    context.registerBean(DefaultGsonMarshallerImplementer.class);
  }
}
