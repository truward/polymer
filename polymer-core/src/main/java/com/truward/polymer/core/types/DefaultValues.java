package com.truward.polymer.core.types;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * Utility class that is aware of the default values that correspond to the given classes.
 *
 * @author Alexander Shabanov
 */
public final class DefaultValues {
  public static final Set<Class<?>> NUMERIC_PRIMITIVES = ImmutableSet.<Class<?>>of(int.class, char.class, byte.class,
      long.class, double.class, float.class);

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

      throw new UnsupportedOperationException("Unsupported primitive type: " + clazz + ", supported types: " +
          NUMERIC_PRIMITIVES);
    }

    return null;
  }
}
