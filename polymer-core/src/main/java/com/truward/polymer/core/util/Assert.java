package com.truward.polymer.core.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Utility class, for verifying assertions.
 * This class is similar to the spring's assertions, but the only difference is that it is easier to use.
 *
 * @author Alexander Shabanov
 */
public final class Assert {

  @Nonnull
  public static <T> T nonNull(@Nullable T instance, String reason) {
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
