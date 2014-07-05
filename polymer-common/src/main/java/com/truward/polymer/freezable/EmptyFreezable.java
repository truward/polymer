package com.truward.polymer.freezable;

/**
 * Freezable support for those classes that need to implement freezable by their interface contract but
 * are immutable by their origin, and don't need to have any logic in the {@link Freezable#freeze()} method.
 *
 * @author Alexander Shabanov
 */
public abstract class EmptyFreezable implements Freezable {

  @Override
  public final boolean isFrozen() {
    return true;
  }

  @Override
  public final void freeze() {
  }
}
