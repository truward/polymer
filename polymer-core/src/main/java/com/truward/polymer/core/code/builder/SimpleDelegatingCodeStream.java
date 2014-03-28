package com.truward.polymer.core.code.builder;

import javax.annotation.Nonnull;

/**
 * Simple implementation of code stream support
 *
 * @author Alexander Shabanov
 */
public final class SimpleDelegatingCodeStream extends CodeStreamSupport {
  private final CodeStream delegate;

  public SimpleDelegatingCodeStream(@Nonnull CodeStream delegate) {
    this.delegate = delegate;
  }

  @Nonnull
  @Override
  protected CodeStream getRootCodeStream() {
    return delegate;
  }
}
