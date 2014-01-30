package com.truward.polymer.core.generator;

import com.google.common.base.Charsets;
import com.truward.polymer.naming.FqName;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.Assert.assertTrue;

/**
 * @author Alexander Shabanov
 */
public class JavaCodeGeneratorTest {

  @Test
  public void shouldGenerateCode() {
    final JavaCodeGenerator g = new JavaCodeGenerator();

    g.packageDirective(FqName.parse("com.mysite.product"));
    g.text("public").ch(' ').text("class").ch(' ').text("Foo").ch(' ', '{');

    g.textWithSpaces("public", "static", "void", "main").ch('(').text("String[] args").ch(')').ch(' ', '{');
    g.singleLineComment("sample multiline", " comment");
    g.textWithSpaces("return", "0").ch(';');
    g.ch('}');

    g.ch('}');

    final String content = printToString(g);

    // verify content
    assertTrue("Should contain package", content.contains("package"));
  }

  //
  // private
  //

  private static String printToString(JavaCodeGenerator generator) {
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
