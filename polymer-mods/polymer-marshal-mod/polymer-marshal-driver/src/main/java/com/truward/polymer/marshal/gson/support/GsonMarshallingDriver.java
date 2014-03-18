package com.truward.polymer.marshal.gson.support;

import com.truward.di.InjectionContext;
import com.truward.polymer.core.driver.SpecificationDriver;
import com.truward.polymer.marshal.json.support.DefaultJsonMarshallingSpecifier;

import javax.annotation.Nonnull;

/**
 * GSON marshalling driver that takes care of registering everything related to GSON marshalling specifications
 * in the global dependency injection context.
 *
 * @author Alexander Shabanov
 */
public final class GsonMarshallingDriver implements SpecificationDriver {
  @Override
  public void join(@Nonnull InjectionContext context) {
    context.registerBean(DefaultJsonMarshallingSpecifier.class);
    context.registerBean(DefaultGsonMarshallerImplementer.class);
  }
}
