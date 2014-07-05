package com.truward.polymer.freezable;

/**
 * Basic non-thread safe support for {@link Freezable} interface for classes with nullable fields and
 * complex 'can't be frozen' checks.
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

  /**
   * Empty method which is supposed to be overriden if the underlying implementation needs to make final checks before
   * finally freezing object.
   */
  protected void beforeFreezing() {
  }

  /**
   * Empty method which is supposed to be overriden if the underlying implementation needs to make final checks before
   * melting (unfreezing) object.
   */
  protected void beforeMelt() {
  }

  protected final void melt() {
    beforeMelt();
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

  @Override
  public final boolean isFrozen() {
    return frozen;
  }

  @Override
  public final void freeze() {
    if (frozen) {
      return;
    }

    beforeFreezing();
    frozen = true;
  }
}
