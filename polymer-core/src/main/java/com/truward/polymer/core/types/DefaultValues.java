package com.truward.polymer.core.types;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * Utility class that is aware of the default values that correspond to the given classes.
 *
 * @author Alexander Shabanov
 */
public final class DefaultValues {
  public static final Set<Class<?>> NUMERIC_PRIMITIVES = ImmutableSet.<Class<?>>of(byte.class, char.class,
      short.class, int.class, long.class, double.class, float.class);

  private DefaultValues() {}

  @SuppressWarnings("UnnecessaryBoxing")
  public static Object getDefaultValueFor(Class<?> clazz) {
    if (clazz.isPrimitive()) {
      if (int.class.equals(clazz)) {
        return Integer.valueOf(0);
      } else if (byte.class.equals(clazz)) {
        return Byte.valueOf((byte) 0);
      } else if (char.class.equals(clazz)) {
        return Character.valueOf('\u0000');
      } else if (short.class.equals(clazz)) {
        return Short.valueOf((short) 0);
      } else if (long.class.equals(clazz)) {
        return Long.valueOf(0L);
      } else if (float.class.equals(clazz)) {
        return Float.valueOf(0.0f);
      } else if (double.class.equals(clazz)) {
        return Double.valueOf(0.0d);
      }

      throw new UnsupportedOperationException("Unsupported primitive type: " + clazz + ", supported types: " +
          NUMERIC_PRIMITIVES);
    }

    return null;
  }
}
