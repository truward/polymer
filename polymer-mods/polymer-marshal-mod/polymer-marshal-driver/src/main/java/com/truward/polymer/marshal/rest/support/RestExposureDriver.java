package com.truward.polymer.marshal.rest.support;

import com.truward.di.InjectionContext;
import com.truward.polymer.core.driver.SpecificationDriver;
import com.truward.polymer.core.driver.SpecificationParameterProvider;
import com.truward.polymer.core.driver.SpecificationState;
import com.truward.polymer.core.driver.SpecificationStateAware;
import com.truward.polymer.marshal.Exposed;
import com.truward.polymer.marshal.rest.RestSpecifier;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public final class RestExposureDriver implements SpecificationDriver {

  @Override
  public void join(@Nonnull InjectionContext context) {
    throw new IllegalStateException();
  }
}
