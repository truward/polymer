package com.truward.polymer.testutil;

import com.google.common.base.Charsets;
import com.truward.polymer.core.generator.JavaCodeGenerator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * @author Alexander Shabanov
 */
public class CodeUtil {
  private CodeUtil() {}

  public static String printToString(JavaCodeGenerator generator) {
    try {
      try (final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
        try (final PrintStream ps = new PrintStream(bos, false, Charsets.UTF_8.name())) {
          generator.printContents(ps);
        }
        return bos.toString(Charsets.UTF_8.name());
      }
    } catch (IOException e) {
      throw new AssertionError(e);
    }
  }
}
