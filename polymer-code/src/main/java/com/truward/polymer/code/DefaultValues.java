package com.truward.polymer.code;

/**
 * Utility class that is aware of the default values that correspond to the given classes.
 *
 * @author Alexander Shabanov
 */
public final class DefaultValues {
  private DefaultValues() {}

  public static Object getDefaultValueFor(Class<?> clazz) {
    if (clazz.isPrimitive()) {
      if (int.class.equals(clazz)) {
        return 0;
      } else if (byte.class.equals(clazz)) {
        return (byte) 0;
      } else if (char.class.equals(clazz)) {
        return (char) 0;
      } else if (short.class.equals(clazz)) {
        return (short) 0;
      } else if (long.class.equals(clazz)) {
        return (long) 0;
      } else if (float.class.equals(clazz)) {
        return 0.0f;
      } else if (double.class.equals(clazz)) {
        return 0.0d;
      }

      throw new UnsupportedOperationException("Unknown primitive type: " + clazz);
    }

    return null;
  }
}
