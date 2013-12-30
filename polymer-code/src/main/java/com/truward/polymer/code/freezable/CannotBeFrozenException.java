package com.truward.polymer.code.freezable;

/**
 * An exception that should be thrown when the half-constructed object is attempted to be frozen.
 *
 * @author Alexander Shabanov
 */
public class CannotBeFrozenException extends RuntimeException {
  public CannotBeFrozenException() {
  }

  public CannotBeFrozenException(String message) {
    super(message);
  }

  public CannotBeFrozenException(String message, Throwable cause) {
    super(message, cause);
  }

  public CannotBeFrozenException(Throwable cause) {
    super(cause);
  }
}
