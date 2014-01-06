package com.truward.polymer.domain.analysis.support;

import javax.annotation.Nonnull;

/**
 * Helper class for operations with names
 *
 * @author Alexander Shabanov
 */
public final class NamingUtil {
  private NamingUtil() {} // Hidden ctor

  public static final String GET_PREFIX = "get";
  public static final String IS_PREFIX = "is";

  public static boolean isJavaBeanGetter(@Nonnull String methodName) {
    return methodName.startsWith(GET_PREFIX) || methodName.startsWith(IS_PREFIX);
  }

  /**
   * Infers possible field name based on the given getter
   *
   * @param methodName Getter method name, expected to have no arguments.
   * @return Potential field name
   */
  @Nonnull
  public static String asFieldName(@Nonnull String methodName) {
    if (methodName.startsWith(GET_PREFIX)) {
      return fieldNameFromGetter(methodName, GET_PREFIX);
    } else if (methodName.startsWith(IS_PREFIX)) {
      return fieldNameFromGetter(methodName, IS_PREFIX);
    }

    throw new RuntimeException("Unsupported non-is/get method " + methodName); // TODO: exception
  }

  //
  // Private
  //

  private static String fieldNameFromGetter(String methodName, String prefix) {
    final int prefixLength = prefix.length();
    final char[] fieldNameChars = new char[methodName.length() - prefixLength];
    methodName.getChars(prefixLength, methodName.length(), fieldNameChars, 0);
    fieldNameChars[0] = Character.toLowerCase(fieldNameChars[0]);
    return new String(fieldNameChars);
  }
}
