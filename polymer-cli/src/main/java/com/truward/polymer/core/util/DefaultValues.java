package com.truward.polymer.core.util;

/**
 * @author Alexander Shabanov
 */
public final class DefaultValues {
  private DefaultValues() {}

  public static Object getDefaultValueFor(Class<?> clazz) {
    if (clazz.isPrimitive()) {
      if (int.class.equals(clazz)) {
        return 0;
      }

      throw new UnsupportedOperationException();
    }

    return null;
  }
}
