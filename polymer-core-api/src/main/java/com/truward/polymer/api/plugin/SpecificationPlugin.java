package com.truward.polymer.api.plugin;

import com.truward.di.InjectionContext;

import javax.annotation.Nonnull;

/**
 * Core interface to non-DI injected plugin which contains all the required classes, that implement the required
 * plugin interaction logic.
 *
 * @author Alexander Shabanov
 */
public interface SpecificationPlugin {

  /**
   * Registers all the plugin-related classes in the given context.
   *
   * @param context Context, where all the related classes should reside.
   */
  void join(@Nonnull InjectionContext context);
}
