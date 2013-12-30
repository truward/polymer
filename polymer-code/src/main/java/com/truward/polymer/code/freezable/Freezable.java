package com.truward.polymer.code.freezable;

/**
 * Indicates that the objects of the implemented class can be made unmodifiable by
 * invoking {@link #freeze()} method, usually to increase efficiency.
 * <p>
 * If object is made unmodifiable, all the setters should not modify a state of the frozen object and
 * rather than modify the corresponding object field should throw runtime exception - which one is is up
 * to the implementation, but it is encouraged to throw {@link IllegalStateException}.
 * </p>
 *
 * @author Alexander Shabanov
 */
public interface Freezable {

  /**
   * Makes the corresponding object unmodifiable.
   * Should be reentrant, so multiple invocations of this method should be allowed.
   * <p>
   * Throws {@link CannotBeFrozenException} if an object is partially constructed and cannot be frozen in
   * the given state.
   * </p>
   */
  void freeze();
}
