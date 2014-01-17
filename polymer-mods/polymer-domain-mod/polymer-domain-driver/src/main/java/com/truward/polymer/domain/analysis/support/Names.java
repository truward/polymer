package com.truward.polymer.domain.analysis.support;

import javax.annotation.Nonnull;

/**
 * Helper class for operations with names
 *
 * @author Alexander Shabanov
 */
public final class Names {
  private Names() {} // Hidden ctor

  //
  // Common variable names
  //

  public static final String ELEMENT = "element";
  public static final String ELEMENTS = "elements";
  public static final String KEY = "key";
  public static final String VALUE = "value";
  public static final String RESULT = "result";


  //
  // Prefixes
  //

  public static final String GET_PREFIX = "get";
  public static final String IS_PREFIX = "is";
  public static final String SET_PREFIX = "set";

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

  @Nonnull
  public static String createPrefixedName(@Nonnull String prefix, @Nonnull String originName) {
    if (originName.isEmpty()) {
      throw new IllegalArgumentException("Prefixed name can not be null");
    }
    return prefix + Character.toUpperCase(originName.charAt(0)) +
        (originName.length() > 1 ? originName.substring(1) : "");
  }

  //
  // Private
  //

  @Nonnull
  private static String fieldNameFromGetter(@Nonnull String methodName, @Nonnull String prefix) {
    final int prefixLength = prefix.length();
    final char[] fieldNameChars = new char[methodName.length() - prefixLength];
    methodName.getChars(prefixLength, methodName.length(), fieldNameChars, 0);
    fieldNameChars[0] = Character.toLowerCase(fieldNameChars[0]);
    return new String(fieldNameChars);
  }
}
