package com.truward.polymer.domain.analysis.support;

import java.lang.reflect.Type;

/**
 * Helper class for operations with names
 *
 * @author Alexander Shabanov
 */
public final class NamingUtil {
  private NamingUtil() {} // Hidden ctor

  private static final String GET_PREFIX = "get";
  private static final String IS_PREFIX = "is";

  /**
   * Infers possible field name based on the given getter
   *
   * @param methodName Getter method name, expected to have no arguments.
   * @return Potential field name
   */
  public static String asFieldName(String methodName) {
    if (methodName.startsWith(GET_PREFIX)) {
      return fieldNameFromGetter(methodName, GET_PREFIX);
    } else if (methodName.startsWith(IS_PREFIX)) {
      return fieldNameFromGetter(methodName, IS_PREFIX);
    }

    throw new RuntimeException("Unsupported non-is/get method " + methodName); // TODO: exception
  }

  /**
   * Creates new getter name based on the field provided.
   *
   * @param fieldType Field type
   * @param fieldName Field name
   * @return New getter name
   */
  public static String createGetterName(Type fieldType, String fieldName) {
    final String prefix = fieldType.equals(Boolean.TYPE) ? IS_PREFIX : GET_PREFIX;
    final StringBuilder nameBuilder = new StringBuilder(prefix.length() + fieldName.length());
    nameBuilder.append(prefix);
    nameBuilder.append(Character.toUpperCase(fieldName.charAt(0)));
    nameBuilder.append(fieldName.subSequence(1, fieldName.length()));
    return nameBuilder.toString();
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
