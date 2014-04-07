package com.truward.polymer.freezable;

/**
 * Basic support for {@link Freezable} interface for classes with nullable fields and complex 'can't be frozen' checks.
 *
 * @author Alexander Shabanov
 */
public abstract class FreezableSupport implements Freezable {
  private boolean frozen;

  protected final void checkNonFrozen() {
    if (isFrozen()) {
      throw new IllegalStateException("Modification is not allowed for the frozen object");
    }
  }

  protected final void checkIsFrozen() {
    if (!isFrozen()) {
      throw new IllegalStateException("Operation is not allowed for the non-frozen object");
    }
  }

  protected void setFrozen() {
    frozen = true;
  }

  protected void melt() {
    frozen = false;
  }

  /**
   * Utility method, that verifies the condition and throws {@link CannotBeFrozenException} if condition is true.
   *
   * @param condition Condition to be verified
   * @param message Corresponding message
   */
  public static void cannotBeFrozenIf(boolean condition, String message) {
    if (condition) {
      throw new CannotBeFrozenException(message);
    }
  }

  public final boolean isFrozen() {
    return frozen;
  }

  @Override
  public final void freeze() {
    if (frozen) {
      return;
    }

    setFrozen();
  }
}
