package com.truward.polymer.freezable;

/**
 * Freezable support for those classes that don't want to have an empty 'dummy' implementation
 * of {@link Freezable#freeze()} method.
 *
 * @author Alexander Shabanov
 */
public abstract class EmptyFreezable implements Freezable {
  @Override
  public void freeze() {
  }
}
