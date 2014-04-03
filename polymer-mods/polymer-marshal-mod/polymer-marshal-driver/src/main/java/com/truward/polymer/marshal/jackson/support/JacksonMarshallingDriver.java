package com.truward.polymer.marshal.jackson.support;

import com.truward.di.InjectionContext;
import com.truward.polymer.core.driver.SpecificationDriver;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public final class JacksonMarshallingDriver implements SpecificationDriver {

  @Override
  public void join(@Nonnull InjectionContext context) {
    context.registerBean(DefaultJacksonMarshallingSpecifier.class);
  }
}
