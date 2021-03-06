package com.truward.polymer.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Utility class, for verifying assertions.
 * This class is similar to the spring's assertions, the only difference is that it
 * returns verified object what makes it easier to use in initializers.
 *
 * @author Alexander Shabanov
 */
public final class Assert {

  @Nonnull
  public static <T> T nonNull(@Nullable T instance, @Nonnull String exceptionMessage) {
    if (instance == null) {
      throw new IllegalArgumentException(exceptionMessage);
    }
    return instance;
  }

  @Nonnull
  public static <T> T nonNull(@Nullable T instance) {
    return nonNull(instance, "Provided instance can not be null");
  }

  public static void state(boolean condition, @Nonnull String exceptionMessage) {
    if (!condition) {
      throw new IllegalStateException(exceptionMessage);
    }
  }

  private Assert() {}
}
