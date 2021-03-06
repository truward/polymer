package com.truward.polymer.core.driver.spi;

import com.truward.di.InjectionContext;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public interface SpecificationDriver {

  void join(@Nonnull InjectionContext context);
}
