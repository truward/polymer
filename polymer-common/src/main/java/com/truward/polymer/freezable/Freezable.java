package com.truward.polymer.freezable;

/**
 * An interface to the object that can made unmodifiable by invoking its {@link #freeze()} method.
 * <p>
 * If object is made unmodifiable, all the setters should not modify a state of the frozen object and
 * rather than modify the corresponding object field should throw {@link IllegalStateException}.
 * </p>
 *
 * @author Alexander Shabanov
 */
public interface Freezable {

  /**
   * Checks if an object has been frozen. Unfrozen objects are mutable, frozen ones can no longer be modified.
   *
   * @return True, if an object is already frozen, false otherwise.
   */
  boolean isFrozen();

  /**
   * Makes an object unmodifiable.
   * <p>
   * This method is reentrant, which means multiple invocations of this method are allowed but invocations of this
   * method won't make any effect on the already frozen object.
   * </p>
   * <p>
   * However this interface does not require implementations to be thread safe and it is up to the implementation
   * to provide additional thread safety guarantees.
   * </p>
   * @throws CannotBeFrozenException if an object is partially constructed and cannot be frozen in the given state.
   */
  void freeze() throws CannotBeFrozenException;
}
