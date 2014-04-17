package com.truward.polymer.code.util;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Utility for escaping strings and characters.
 *
 * @author Alexander Shabanov
 */
public final class EscapeUtil {
  private EscapeUtil() {}

  /**
   * @param str String to be escaped.
   * @return String, with properly escaped characters
   */
  @Nonnull public static String escape(@Nonnull String str) throws IOException {
    final StringBuilder builder = new StringBuilder();
    escape(str, builder);
    return builder.toString();
  }

  public static void escape(@Nonnull String str, @Nonnull Appendable appendable) throws IOException {
    final int length = str.length();
    for (int i = 0; i < length; ++i) {
      final char ch = str.charAt(i);
      if (isPrintableAsciiChar(ch)) {
        appendable.append(ch); // optimization - no char-to-string conversion for ASCII chars
      } else {
        appendable.append(escape(ch));
      }
    }
  }

  /**
   * @param ch Character to be quoted.
   * @return Character as string for printable ASCII character, escaped character otherwise
   */
  @Nonnull public static String escape(char ch) {
    if (isPrintableAsciiChar(ch)) {
      return String.valueOf(ch);
    }

    switch (ch) {
      case '\\':  return "\\\\";
      case '\'':  return "\\'";
      case '\"':  return "\\\"";
      case '\r':  return "\\r";
      case '\n':  return "\\n";
      case '\t':  return "\\t";
      case '\b':  return "\\b";
      case '\f':  return "\\f";
      default:
        return String.format("\\u%04x", (int) ch); // '\u0442'
    }
  }

  //
  // Private
  //

  /**
   * @param ch Char to be tested
   * @return True, if character is a printable ASCII, false otherwise
   */
  private static boolean isPrintableAsciiChar(char ch) {
    return ch >= ' ' && ch <= '~';
  }
}
