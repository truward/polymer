package com.truward.polymer.core.util;

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
  public static <T> T nonNull(@Nullable T instance, @Nonnull String reason) {
    if (instance == null) {
      throw new IllegalArgumentException(reason);
    }
    return instance;
  }

  @Nonnull
  public static <T> T nonNull(@Nullable T instance) {
    return nonNull(instance, "Provided instance can not be null");
  }

  private Assert() {}
}
